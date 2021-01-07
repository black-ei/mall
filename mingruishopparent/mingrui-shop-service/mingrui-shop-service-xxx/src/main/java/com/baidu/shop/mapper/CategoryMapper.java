package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<CategoryEntity>, SelectByIdListMapper<CategoryEntity,Integer> {

    @Select(value = "SELECT *  FROM tb_category WHERE id IN(SELECT category_id FROM tb_category_brand WHERE brand_id = #{brandId})")
    List<CategoryEntity> queryCategoryByBrandId(Integer brandId);
}
