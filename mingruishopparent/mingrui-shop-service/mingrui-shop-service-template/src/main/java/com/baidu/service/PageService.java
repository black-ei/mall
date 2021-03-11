package com.baidu.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


public interface PageService {


    Map<String, Object> getGoods(@RequestParam Integer spuId);
}
