package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.feign.GoodsFei;
import com.baidu.shop.feign.SpecParmaFeign;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.CarService;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtils;
import com.baidu.shop.utils.StringUtils;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private RedisRepository redisRepository;

    private  final  String GOODS_CAR_PRE = "goods_car_";

    @Autowired
    private GoodsFei goodsFei;

    @Autowired
    private SpecParmaFeign specParmaFeign;

    @Override//购物车++ -- 操作
    public Result<JSONPObject> operationGoodsCar(@NotEmpty Boolean type, @NotNull Long skuId, String token) {
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Car redisCar = redisRepository.getHash(GOODS_CAR_PRE + userInfo.getId(), skuId + "", Car.class);
            redisCar.setNum(type?redisCar.getNum()+1:redisCar.getNum()-1);
            redisRepository.setHash(GOODS_CAR_PRE + userInfo.getId(), skuId + "",JSONObject.toJSONString(redisCar));
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError("");
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<List<Car>> getGoodsCar(String token) {
        List<Car> list = new ArrayList<>();
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Map<String, String> carMap = redisRepository.getHash(GOODS_CAR_PRE + userInfo.getId());
            carMap.forEach((k,v)->{
                list.add(JSONUtil.toBean(v,Car.class));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess(list);
    }

    @Override
    public Result<JSONObject> mergeCar(String carList, String token) {
        JSONObject jsonObject = JSONObject.parseObject(carList);//将急送字符串转成急送对象
        List<Car> list = JSONObject.parseArray(jsonObject.getString("carList"), Car.class);//将对象属性为carList的数据转换为Car类型的list集合
        list.forEach(car -> addCar(car,token));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> addCar(Car car,String token) {

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());//获得当前用户id
            Car carItem = redisRepository.getHash(GOODS_CAR_PRE + userInfo.getId(), car.getSkuId() + "", Car.class);
            if(null==carItem){//购物车中没有当前商品 查询sku后进行新增

                Result<SkuDTO> skus = goodsFei.getSku(car.getSkuId());//通过skuId查询suk
                if(skus.isSuccess()){
                    Map<String, String> ownSpecMapItem = JSONUtil.toMapValueString(skus.getData().getOwnSpec());

                    Map<String, String> ownSpecMap = new HashMap<>();
                    ownSpecMapItem.forEach((k,v)->{//转换ownSpecMap
                        Result<SpecParamEntity> result = specParmaFeign.getSpecParamInfo(Integer.valueOf(k));
                        if (result.isSuccess()){
                            ownSpecMap.put(result.getData().getName(),v);
                        }
                    });

                    car.setUserId(userInfo.getId());
                    car.setStock(skus.getData().getStock());
                    car.setImage(StringUtils.isEmpty(skus.getData().getImages())?skus.getData().getImages().split(",")[0]:"");
                    car.setOwnSpec(JSONUtil.toJsonString(ownSpecMap));
                    car.setPrice(skus.getData().getPrice().longValue());
                    car.setTitle(skus.getData().getTitle());
                    redisRepository.setHash(GOODS_CAR_PRE+userInfo.getId(),car.getSkuId()+"",JSONObject.toJSONString(car));//放入redis
                    log.info("购物车为空,新增值: {},数量:{}",car,car.getNum());
                }
            }else{//购物车由当前商品,重新给num赋值
                carItem.setNum(carItem.getNum()+car.getNum());
                redisRepository.setHash(GOODS_CAR_PRE + userInfo.getId(),car.getSkuId()+"",JSONObject.toJSONString(carItem));
                log.info("重新刷新num: {}",carItem.getNum());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }
}
