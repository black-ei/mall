package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryBrand;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

public interface CategoryBrandMapper extends Mapper<CategoryBrand>, InsertListMapper<CategoryBrand> {
}
