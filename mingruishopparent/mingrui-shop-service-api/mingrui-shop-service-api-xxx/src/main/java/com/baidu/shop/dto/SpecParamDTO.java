package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(value = "规格参数下的参数名")
@Data
public class SpecParamDTO extends BaseDTO  {

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.update.class})
    private Integer id;
    @ApiModelProperty(value = "分类id",example = "1")
    private Integer cid;
    @ApiModelProperty(value = "规格id",example = "1")
    @NotNull(message = "规格id不能为空",groups = {MingruiOperation.add.class})
    private Integer groupId;
    @ApiModelProperty(value = "规格参数名")
    @NotBlank(message = "规格参数名不能为空",groups = {MingruiOperation.add.class,MingruiOperation.update.class})
    private String name;
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;
}
