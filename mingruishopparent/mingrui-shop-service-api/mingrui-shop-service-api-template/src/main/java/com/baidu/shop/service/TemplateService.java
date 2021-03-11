package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

@Api(tags = "商品详情html模板静态化接口")
public interface TemplateService {
    @GetMapping(value = "template/createStaticHTMLTemplate")
    @ApiOperation(value = "根据spuId创建静态html")
    Result<JSONObject> createStaticHTMLTemplate(Integer spuId);

    @GetMapping(value = "template/initStaticHTMLTemplate")
    @ApiOperation(value = "初始化所有商品静态html")
    Result<JSONObject> initStaticHTMLTemplate();

    @GetMapping(value = "template/deleteStaticHTMLTemplate")
    @ApiOperation(value = "根据spuId删除商品静态html")
    Result<JSONObject> deleteStaticHTMLTemplate(Integer spuId);

    @GetMapping(value = "template/clearStaticHTMLTemplate")
    @ApiOperation(value = "删除所有商品静态html")
    Result<JSONObject> clearStaticHTMLTemplate();


}
