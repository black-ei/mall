package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.Car;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Api(tags = "购物车接口")
public interface CarService {

    @ApiOperation(value = "添加商品到购物车")
    @PostMapping(value = "car/addCar")
    Result<JSONObject> addCar(@RequestBody Car car,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "登陆同步购物车到redis")
    @PostMapping(value = "car/mergeCar")
    Result<JSONObject> mergeCar(@RequestBody String carList,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @GetMapping("car/getGoodsCar")
    @ApiOperation(value = "登陆状态下获取redis购物车数据")
    Result<List<Car>> getGoodsCar(@CookieValue(value = "MRSHOP_TOKEN") String token);
}
