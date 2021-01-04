package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@ApiModel(value = "品牌DTO")
@Data
public class BrandDTO extends BaseDTO {

    @ApiModelProperty(value = "品牌主键",example = "1")
    @NotNull(message = "ID不能为空",groups = {MingruiOperation.update.class})
    @Null(message = "save不能传递id",groups = {MingruiOperation.add.class})
    private Integer id;

    @ApiModelProperty(value = "品牌名称")
    @NotBlank(message = "品牌名称不能空",groups = {MingruiOperation.update.class,MingruiOperation.add.class})
    private String name;

    @ApiModelProperty(value = "品牌图片")
    private String image;

    @ApiModelProperty(value = "品牌首字母")
    private Character letter;

    @ApiModelProperty(value = "分类ids")
    @NotBlank(message = "品牌分类不能为空",groups = {MingruiOperation.add.class,MingruiOperation.update.class,})
    private String categories;

}
