package com.baidu.shop.mapper;

import com.baidu.shop.entity.BrandEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<BrandEntity>, SelectByIdListMapper<BrandEntity,Integer> {
    @Select("SELECT id,`name` FROM tb_brand WHERE id IN(SELECT brand_id FROM tb_category_brand WHERE category_id = #{cid})")
    List<BrandEntity> queryBrandByCategoryId(Integer cid);
}
