package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.plugin.Intercepts;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "规格接口")
@Validated
public interface SpecificationService {

    @ApiOperation("通过条件查询规格组")
    @GetMapping(value = "/spec/groups")
    Result<List<SpecGroupEntity>> getSepcGroupInfo(@SpringQueryMap @Validated(value = {MingruiOperation.get.class})SpecGroupDTO specGroupDTO);

    @ApiOperation("新增规格组")
    @PostMapping(value = "/spec/save")
    Result<List<JsonObject>> saveSepcGroupInfo(@Validated(value = {MingruiOperation.add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation("修改规格组")
    @PutMapping(value = "/spec/save")
    Result<List<JsonObject>> editSepcGroupInfo(@Validated(value = {MingruiOperation.update.class}) @RequestBody  SpecGroupDTO specGroupDTO);

    @ApiOperation("删除规格组")
    @DeleteMapping(value = "/spec/del")
    Result<List<JsonObject>> delSepcGroupInfo(@NotNull Integer id);

    @ApiOperation("查询规格参数")
    @GetMapping("/specparam/getSpecParamInfo")
    Result<List<SpecParamEntity>> getSpecParamInfo(@SpringQueryMap SpecParamDTO specParamDTO);

    @ApiOperation("新增分组参数字段")
    @PostMapping("/specparam/save")
    Result<JSONObject> saveSpecParamInfo(@RequestBody @Validated(value = {MingruiOperation.add.class}) SpecParamDTO specParamDTO);

    @ApiOperation("修改分组参数字段")
    @PutMapping("/specparam/save")
    Result<JSONObject> editSpecParamInfo(@RequestBody @Validated(value = {MingruiOperation.update.class}) SpecParamDTO specParamDTO);

    @ApiOperation("删除分组参数字段")
    @DeleteMapping("/specparam/del")
    Result<JSONObject> deleteSpecParamInfo(@NotNull(message = "主键不能为空") Integer id);

    @ApiOperation("通过id查询规格参数")
    @GetMapping("/specparam/getSpecParamInfoById")
    Result<SpecParamEntity> getSpecParamInfo(@RequestParam @NotNull Integer id);
}
