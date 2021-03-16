package com.baidu.shop.mapper;

import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.entity.SkuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuMapper extends Mapper<SkuEntity>, DeleteByIdListMapper<SkuEntity,Long> {
    @Select("SELECT k.*,t.* FROM tb_sku k,tb_stock t WHERE k.`id` = t.`sku_id` AND k.`spu_id` = #{spuId}")
    List<SkuDTO> getSkuAndStock(Integer spuId);

    @Select("SELECT k.*,t.* FROM tb_sku k,tb_stock t WHERE k.`id` = t.`sku_id` AND k.`id` = #{skuId}")
    SkuDTO selectSkuAndStock(Long skuId);
}
