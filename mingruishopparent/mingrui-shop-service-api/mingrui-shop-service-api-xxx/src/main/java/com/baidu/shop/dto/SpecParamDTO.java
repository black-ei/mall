package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(value = "规格参数下的参数名")
@Data
public class SpecParamDTO extends BaseDTO  {

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.update.class})
    private Integer id;

    @ApiModelProperty(value = "分类id",example = "1")
    @NotNull(message = "分类id不能为空",groups = {MingruiOperation.add.class})
    private Integer cid;

    @ApiModelProperty(value = "规格id",example = "1")
    @NotNull(message = "规格id不能为空",groups = {MingruiOperation.add.class})
    private Integer groupId;

    @ApiModelProperty(value = "规格参数名")
    @NotBlank(message = "规格参数名不能为空",groups = {MingruiOperation.add.class,MingruiOperation.update.class})
    private String name;

    @ApiModelProperty(value = "是否为数字类型,1->true0->false",example = "0")
    @NotNull(message = "是否为数字类型参数不能为空",groups = {MingruiOperation.add.class,MingruiOperation.update.class})
    private Boolean numeric;

    @ApiModelProperty(value = "数字类型参数的单位,非数字类型可以为空")
    private String unit;

    @ApiModelProperty(value = "是否是sku通用属性,是为true,否为false",example = "0")
    @NotNull(message = "是否为sku通用属性不能为空",groups = {MingruiOperation.add.class,MingruiOperation.update.class})
    private Boolean generic;

    @ApiModelProperty(value = "是否用于搜索过滤",example = "0")
    @NotNull(message = "是否用于搜索不能为空",groups = {MingruiOperation.add.class,MingruiOperation.update.class})
    private Boolean searching;
    @ApiModelProperty("数值类型参数,如果需要搜索,则添加分段间隔值")
    private String segments;
}
