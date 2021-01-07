package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "品牌接口")
@Validated
public interface BrandService {

    @ApiOperation(value = "获取品牌信息")
    @GetMapping(value = "brand/list")
    Result<PageInfo<BrandEntity>> page(@Validated({MingruiOperation.Search.class}) BrandDTO brandDTO);

    @PostMapping(value = "brand/save")
    @ApiOperation(value = "新增品牌信息")
    Result<JSONObject> save(@Validated({MingruiOperation.add.class}) @RequestBody BrandDTO brandDTO);

    @PutMapping(value = "brand/save")
    @ApiOperation(value = "修改品牌信息")
    Result<JSONObject> edit(@Validated({MingruiOperation.update.class}) @RequestBody BrandDTO brandDTO);

    @DeleteMapping(value = "brand/delete")
    @ApiOperation(value = "根据id删除品牌信息")
    Result<JSONObject> delete(@NotNull(message = "id不能为空") Integer id);

    @ApiOperation(value = "根据分类id获取绑定的品牌信息")
    @GetMapping(value = "brand/queryBrandByCategoryId")
    Result<List<BrandEntity>> queryBrandByCategoryId(@NotNull Integer cid);
}
