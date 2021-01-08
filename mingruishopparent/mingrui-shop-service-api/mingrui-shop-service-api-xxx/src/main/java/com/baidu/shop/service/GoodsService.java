package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "商品接口")
@Validated
public interface GoodsService {

    @ApiOperation(value = "获取spu信息")
    @GetMapping(value = "goods/getSpuInfo")
    Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO);

    @ApiOperation(value = "新增商品")
    @PostMapping(value = "goods/save")
    Result<JSONObject> saveSpuInfo(@RequestBody @Validated(value = {MingruiOperation.add.class}) SpuDTO spuDTO);

    @ApiOperation(value = "根据spuId获取spudetail信息")
    @GetMapping(value = "goods/getSpuDetail")
    Result<SpuDetailEntity> getSpuInfo(@NotNull Integer spuId);

    @ApiOperation(value = "根据spuId获取sku信息和stock信息")
    @GetMapping(value = "goods/getSkus")
    Result<List<SkuDTO>> getSkus(@NotNull Integer spuId);

    @ApiOperation(value = "修改商品")
    @PutMapping(value = "goods/save")
    Result<JSONObject> editSpuInfo(@RequestBody @Validated(value = {MingruiOperation.update.class}) SpuDTO spuDTO);

    @ApiOperation(value = "删除商品")
    @DeleteMapping(value = "goods/del")
    Result<JSONObject> delSpuInfo(@NotNull(message = "id不能为空") Integer spuId);

    @ApiOperation(value = "下架商品")
    @GetMapping(value = "goods/down")
    Result<List<SkuDTO>> downGood(@NotNull Integer spuId);

}
