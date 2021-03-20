package com.bai.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.bai.shop.entity.AddrEntity;
import com.baidu.shop.base.Result;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "地址接口")
@Validated
public interface AddrService {

    @PostMapping(value = "addr/save")
    @ApiOperation(value = "新增地址")
    Result<JSONObject> saveAddr(@RequestBody  AddrEntity addrEntity, @CookieValue("MRSHOP_TOKEN") String token);

    @GetMapping(value = "addr/queryAddrs")
    @ApiOperation(value = "通过当前用户获取用户所有地址")
    Result<List<AddrEntity>> getAddrs(Integer addrId,@CookieValue("MRSHOP_TOKEN") String token);

    @DeleteMapping(value = "addr/delAddrs")
    @ApiOperation(value = "通过地址Id删除用户地址")
    Result<JSONObject> delAddrs(@NotNull Integer addrId,@CookieValue("MRSHOP_TOKEN") String token);

    @PutMapping(value = "addr/idea")
    @ApiOperation(value = "修改地址")
    Result<JSONObject> editAddr(@RequestBody  @Validated({MingruiOperation.update.class}) AddrEntity addrEntity, @CookieValue("MRSHOP_TOKEN") String token);

    @GetMapping(value = "addr/theDefault")
    @ApiOperation(value = "根据Id将当前地址设为默认")
    Result<List<AddrEntity>> theDefault(@NotNull Integer addrId,@CookieValue("MRSHOP_TOKEN") String token);
}
