package com.baidu.shop.business.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.feign.SkuFeign;
import com.baidu.feign.SpecParamFei;
import com.baidu.redis.repository.RedisRepository;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.BaseBean;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.OrderService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.MqMessageConstant;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.dto.OrderInfo;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.IdWorker;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.JwtUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class OrderServiceImpl extends BaseApiService implements OrderService {

    private  final  String GOODS_CAR_PRE = "goods_car_";
    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private IdWorker idWorker;

    @Resource
    private RedisRepository redisRepository;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private SkuFeign skuFeign;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private SpecParamFei specParamFei;

    @Override//根据用户Id查询用户订单
    public Result<PageInfo<List<OrderInfo>>> getOrderByOrderId(Integer page,String token) {
        UserInfo userInfo = null;
        try {
            userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError("身份失效!!!");
        }
        Page<Object> pageInfo = null;
       if(page!=null && page>0){
           pageInfo = PageHelper.startPage(page, 4);//分页
       }
        Example example = new Example(OrderEntity.class);
        example.createCriteria().andEqualTo("userId",userInfo.getId());
        example.orderBy("createTime").desc();//按最近时间查询
        List<OrderInfo> orderInfoList = orderMapper.selectByExample(example).stream().map(order -> {
            OrderInfo orderInfo = BaseBean.copyProperties(order, OrderInfo.class);
            orderInfo.setOrderDetailList(this.getOrderDetailEntity(order.getOrderId() + ""));
            orderInfo.setOrderStatusEntity(orderStatusMapper.selectByPrimaryKey(order.getOrderId()));
            return orderInfo;
        }).collect(Collectors.toList());

        return this.setResult(200,pageInfo.getTotal()+"",orderInfoList);
    }

    //通过订单Id查询订单Sku(商品)集合
    private List<OrderDetailEntity> getOrderDetailEntity(@NotNull String orderId){
        Example example = new Example(OrderDetailEntity.class);
        example.createCriteria().andEqualTo("orderId",orderId);

        List<OrderDetailEntity> list = orderDetailMapper.selectByExample(example).stream().map(orderDetail -> {
            HashMap<String, String> ownSpecMap = new HashMap<>();
            JSONUtil.toMapValueString(orderDetail.getOwnSpec()).forEach((k, v) -> {
                Result<SpecParamEntity> result = specParamFei.getSpecParamInfo(Integer.valueOf(k));
                if (result.isSuccess()) {
                    ownSpecMap.put(result.getData().getName(), v);//查到参数名
                }
            });
            orderDetail.setOwnSpec(JSONUtil.toJsonString(ownSpecMap));
            return orderDetail;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public Result<JSONObject> updateOrderStatus(@NotEmpty String orderId) {
        OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByPrimaryKey(orderId);
        orderStatusEntity.setStatus(2);
        orderStatusMapper.updateByPrimaryKeySelective(orderStatusEntity);
        return this.setResultSuccess();
    }

    @Override
    public Result<OrderInfo> getOrderInfoByOrderId(@NotEmpty String orderId) {
        OrderEntity orderEntity = orderMapper.selectByPrimaryKey(orderId);
        OrderInfo orderInfo = BaseBean.copyProperties(orderEntity, OrderInfo.class);

        orderInfo.setOrderDetailList(this.getOrderDetailEntity(orderId));
        orderInfo.setOrderStatusEntity(orderStatusMapper.selectByPrimaryKey(orderId));
        return this.setResultSuccess(orderInfo);
    }

    @Override
    @Transactional
    public Result<String> createOrder(OrderDTO orderDTO,String token) {
        long orderId = idWorker.nextId();
        Date date = new Date();
        ArrayList<Long> priceList = new ArrayList<>();
        //获取用户ID
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //创建订单
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setOrderId(orderId+"");
            orderEntity.setPromotionIds("1,2");
            orderEntity.setPaymentType(orderDTO.getPayType());
            orderEntity.setCreateTime(date);
            orderEntity.setUserId(userInfo.getId()+"");
            orderEntity.setBuyerMessage("期待");
            orderEntity.setBuyerNick(userInfo.getUsername());
            orderEntity.setBuyerRate(1);
            orderEntity.setInvoiceType(2);
            orderEntity.setSourceType(2);

            //创建订单状态表
            OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setOrderId(orderId);
            orderStatusEntity.setStatus(1);
            orderStatusEntity.setCreateTime(date);

            //创建订单商品表
            List<OrderDetailEntity> orderDetailList = Arrays.asList(orderDTO.getSkuIds().split(",")).stream().map(skuId -> {
                Car redisCar = redisRepository.getHash(GOODS_CAR_PRE + userInfo.getId(), skuId, Car.class);
                OrderDetailEntity orderDetailEntity = BaseBean.copyProperties(redisCar, OrderDetailEntity.class);
                orderDetailEntity.setOrderId(orderId);//补全orderId信息
                priceList.add(redisCar.getPrice()*redisCar.getNum());//获得每个商品的总价格
                //更新商品库存
                    if (!skuFeign.updateStock(redisCar.getSkuId(),redisCar.getNum(),0).isSuccess()) throw new RuntimeException("update stock ERROR!");
                return orderDetailEntity;
            }).collect(Collectors.toList());
            Long totalPrice = priceList.stream().reduce(0L, (old, curr) -> old + curr);
            orderEntity.setTotalPay(totalPrice);
            orderEntity.setActualPay(totalPrice);


            orderMapper.insertSelective(orderEntity);
            orderDetailMapper.insertList(orderDetailList);
            orderStatusMapper.insertSelective(orderStatusEntity);
            //入库后,删除购物车提交的数据
            Arrays.asList(orderDTO.getSkuIds().split(",")).stream().forEach(skuId ->redisRepository.delHash(GOODS_CAR_PRE + userInfo.getId(), skuId));
            rabbitTemplate.convertAndSend(MqMessageConstant.DELAY_QUEUE,orderId+"");


        } catch (Exception e) {
            e.printStackTrace();
            this.setResultError("用户失效!");
        }

        return this.setResult(HTTPStatus.OK,"",orderId+"");
    }
}
