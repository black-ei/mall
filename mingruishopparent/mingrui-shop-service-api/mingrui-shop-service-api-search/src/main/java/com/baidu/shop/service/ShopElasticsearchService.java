package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "es接口")
public interface ShopElasticsearchService {

//    @ApiOperation(value = "获取商品信息测试")
//    @GetMapping(value = "es/goodsInfo")
   // Result<JSONObject> esGoodsInfo();

    @ApiOperation(value = "通过spuId新增数据到es")
    @PostMapping(value = "es/saveData")
    Result<JSONObject> saveData(@RequestParam @NotNull Integer spuId);

    @ApiOperation(value = "通过spuId删除es数据")
    @DeleteMapping(value = "es/saveData")
    Result<JSONObject> delData(@RequestParam @NotNull Integer spuId);

    @ApiOperation(value = "ES初始化 ->建立索引,创建映射,mysql数据同步")
    @GetMapping(value = "es/initGoodsEsData")
    Result<JSONObject> initGoodsEsData();

    @ApiOperation(value = "删除ES中所有商品数据")
    @GetMapping(value = "es/clearGoodsEsData")
    Result<JSONObject> clearGoodsEsData();

    @ApiOperation(value = "es搜索")
    @GetMapping(value = "es/search")
    Result<List<GoodsDoc>> searchInfo(@RequestParam String search,@RequestParam Integer page,@RequestParam String filter);
}
