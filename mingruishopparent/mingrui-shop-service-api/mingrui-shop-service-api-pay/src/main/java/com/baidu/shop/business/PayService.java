package com.baidu.shop.business;

import com.baidu.shop.dto.PayInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api("支付接口")
public interface PayService {

    @GetMapping(value = "pay/requestPay")
    @ApiOperation(value = "请求支付接口")
    void requestPay(PayInfoDTO payInfoDTO, @CookieValue(value = "MRSHOP_TOKEN") String token,HttpServletResponse httpServletResponse) throws IOException;

    @GetMapping(value = "pay/notify")
    @ApiOperation(value = "异步通知接口") //支付宝帮我们调
    void notify(HttpServletRequest request);

    @GetMapping(value = "pay/returnUrl")
    @ApiOperation(value = "跳转成功页面回调接口")//支付宝帮我们调
    void returnUrl(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

}
