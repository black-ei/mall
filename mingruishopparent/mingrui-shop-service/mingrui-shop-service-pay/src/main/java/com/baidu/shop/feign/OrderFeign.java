package com.baidu.shop.feign;

import com.baidu.shop.base.Result;
import com.baidu.shop.business.OrderService;
import com.baidu.shop.dto.OrderInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;

@FeignClient(value = "order-server")
public interface OrderFeign {

    @ApiOperation(value = "通过订单id查询订单信息")
    @GetMapping(value = "/order/getOrderInfoByOrderId")
    Result<OrderInfo> getOrderInfoByOrderId(@RequestParam @NotEmpty String orderId);

    @ApiOperation(value = "支付成功回调修改订单状态")
    @GetMapping(value = "/order/updateOrderStatus")
    Result<OrderInfo> updateOrderStatus(@RequestParam @NotEmpty String orderId);
}
