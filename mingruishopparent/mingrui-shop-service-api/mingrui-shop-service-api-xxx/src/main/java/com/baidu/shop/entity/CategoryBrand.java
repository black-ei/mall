package com.baidu.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_category_brand")
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBrand {

    private Integer categoryId;
    private Integer brandId;


}
