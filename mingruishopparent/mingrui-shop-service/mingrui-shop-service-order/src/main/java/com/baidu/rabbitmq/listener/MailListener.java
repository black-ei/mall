package com.baidu.rabbitmq.listener;

import com.baidu.feign.SkuFeign;
import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;

@Component
@Slf4j
public class MailListener {

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private SkuFeign skuFeign;

    @RabbitListener(queues = {"biz.order"})
    public void process(String orderId){

        OrderStatusEntity entity = orderStatusMapper.selectByPrimaryKey(orderId);
        if (entity!=null && entity.getStatus()==1){//如果订单是未支付状态(超时) 将状态改为交易关闭
            entity.setStatus(5);
            orderStatusMapper.updateByPrimaryKeySelective(entity);
            log.info("订单id : {},订单超时状态改为: {},",orderId,"交易关闭");

            Example example = new Example(OrderDetailEntity.class);
            example.createCriteria().andEqualTo("orderId",orderId);
            orderDetailMapper.selectByExample(example).stream() //恢复库存
                    .forEach(orderDetail -> skuFeign.updateStock(orderDetail.getSkuId(),orderDetail.getNum(),1));
        }

    }
}
