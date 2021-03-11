package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "商品分类接口")
@Validated
public interface CategoryService {

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "category/list")
    Result<List<CategoryEntity>> getCategoryByPid(@NotNull(message = "ID不能为NULL") Integer pid);

    @ApiOperation(value = "通过id删除一个分类节点")
    @DeleteMapping(value = "category/del")
    Result<JSONObject> delCategoryById(@NotNull(message = "ID不能为NULL") Integer id);

    @ApiOperation(value = "更新")
    @PutMapping(value = "category/edit")
    Result<JSONObject> editCategoryById(@Validated(MingruiOperation.update.class) CategoryEntity categoryEntity);

    @ApiOperation(value = "新增节点")
    @PostMapping("category/save")
    Result<JSONObject> saveCategory(@Validated({MingruiOperation.add.class}) @RequestBody CategoryEntity categoryEntity);

    @GetMapping(value = "category/queryCategoryByBrandId")
    @ApiOperation(value = "通过品牌id查询该品牌的分类数据")
    Result<List<CategoryEntity>> queryCategoryByBrandId(@NotNull Integer brandId);

    @GetMapping(value = "category/getCategoryByIds")
    @ApiOperation(value = "通过分类ids查询分类信息")
    Result<List<CategoryEntity>> getCategoryByIds(@RequestParam String categoryStrId);
}
