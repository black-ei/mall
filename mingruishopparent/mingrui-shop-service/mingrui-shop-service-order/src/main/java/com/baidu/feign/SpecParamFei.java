package com.baidu.feign;

import com.baidu.shop.service.SpecificationService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "xxx-server",contextId = "specParamServer")
public interface SpecParamFei extends SpecificationService {
}
