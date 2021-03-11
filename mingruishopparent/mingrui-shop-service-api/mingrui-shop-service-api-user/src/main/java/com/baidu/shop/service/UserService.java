package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Api(tags = "用户接口")
@Validated
public interface UserService {

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "user/register")
    Result<JSONObject> register(@RequestBody @Validated(value = {MingruiOperation.add.class}) UserDTO userDTO);

    @ApiOperation(value = "验证用户是否存在")
    @GetMapping(value = "user/check/{value}/{type}")
    Result<Integer> registerCheck(@NotEmpty @PathVariable(value = "value") String value, @NotNull @PathVariable(value = "type") Integer type);

    @ApiOperation(value = "发送验证码")
    @PostMapping(value = "user/sendValidCode")
    Result<JsonObject> sendValidCode(@RequestBody UserDTO userDTO);

    @ApiOperation(value = "校验验证码")
    @GetMapping(value = "user/checkVerifyCode")
    Result<JsonObject> sendValidCode(@NotEmpty String phone,@NotNull String code);
}
