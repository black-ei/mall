package com.baidu.feign;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "xxx-server",contextId = "skuServer")
public interface SkuFeign extends GoodsService {
}
