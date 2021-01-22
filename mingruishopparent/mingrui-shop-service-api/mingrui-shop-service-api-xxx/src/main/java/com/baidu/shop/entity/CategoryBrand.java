package com.baidu.shop.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_category_brand")
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "分类品牌中间表")
public class CategoryBrand {

    @ApiModelProperty(value = "分类ID")
    private Integer categoryId;
    @ApiModelProperty(value = "品牌ID")
    private Integer brandId;


}
