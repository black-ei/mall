package com.baidu.shop.business;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.dto.OrderInfo;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Api(tags = "订单接口")
public interface OrderService {

    @ApiOperation(value = "创建订单")
    @PostMapping(value = "/order/createOrder")
    //返回订单id为long类型(会有精度丢失的问题)所以转为String返回
    Result<String> createOrder(@RequestBody OrderDTO orderDTO, @CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "通过订单id查询订单信息")
    @GetMapping(value = "/order/getOrderInfoByOrderId")
    Result<OrderInfo> getOrderInfoByOrderId(@RequestParam @NotEmpty String orderId);

    @ApiOperation(value = "支付成功回调修改订单状态")
    @GetMapping(value = "/order/updateOrderStatus")
    Result<JSONObject> updateOrderStatus(@RequestParam @NotEmpty String orderId);

    @ApiOperation(value = "通过用户id查询订单信息")
    @GetMapping(value = "/order/getOrderByOrderId")
    Result<PageInfo<List<OrderInfo>>> getOrderByOrderId(Integer page, @CookieValue(value = "MRSHOP_TOKEN") String token);


}
