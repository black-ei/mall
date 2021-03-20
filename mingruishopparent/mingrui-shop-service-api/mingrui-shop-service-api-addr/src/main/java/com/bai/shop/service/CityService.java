package com.bai.shop.service;

import com.bai.shop.dto.City;
import com.baidu.shop.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "城市信息接口")
public interface CityService {

    @GetMapping(value = "addr/queryCityByParentId")
    @ApiOperation(value = "根据父ID查询城市信息")
    Result<List<City>> queryCityByParentId(@NotNull Integer parentId);
}
