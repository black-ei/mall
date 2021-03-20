package com.baidu.shop.service.impl;

import com.bai.shop.dto.City;
import com.bai.shop.service.CityService;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.mapper.CityMapper;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class CityServiceImpl extends BaseApiService implements CityService {

    @Resource
    private CityMapper cityMapper;

    @Override
    public Result<List<City>> queryCityByParentId(@NotNull Integer parentId) {
        Example example = new Example(City.class);
        example.createCriteria().andEqualTo("parentId",parentId);

        return this.setResultSuccess(cityMapper.selectByExample(example));
    }
}
