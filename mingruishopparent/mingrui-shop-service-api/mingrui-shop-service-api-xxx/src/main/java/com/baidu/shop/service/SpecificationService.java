package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "规格接口")
@Validated
public interface SpecificationService {

    @ApiOperation("通过条件查询规格组")
    @GetMapping(value = "/spec/groups")
    Result<List<SpecGroupEntity>> getSepcGroupInfo(@Validated(value = {MingruiOperation.get.class})SpecGroupDTO specGroupDTO);

    @ApiOperation("新增规格组")
    @PostMapping(value = "/spec/save")
    Result<List<JsonObject>> saveSepcGroupInfo(@Validated(value = {MingruiOperation.add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiOperation("修改规格组")
    @PutMapping(value = "/spec/save")
    Result<List<JsonObject>> editSepcGroupInfo(@Validated(value = {MingruiOperation.update.class}) @RequestBody  SpecGroupDTO specGroupDTO);

    @ApiOperation("新增规格组")
    @DeleteMapping(value = "/spec/del")
    Result<List<JsonObject>> delSepcGroupInfo(@NotNull Integer id);

}
