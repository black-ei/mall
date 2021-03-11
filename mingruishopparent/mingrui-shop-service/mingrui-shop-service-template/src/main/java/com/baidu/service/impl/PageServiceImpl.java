package com.baidu.service.impl;

import com.baidu.feign.BrandFeign;
import com.baidu.feign.CategoryFeign;
import com.baidu.feign.GoodsFeign;
import com.baidu.feign.SpecificationFeign;
import com.baidu.service.PageService;
import com.baidu.shop.base.BaseBean;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Service
public class PageServiceImpl implements PageService{
   // @Autowired
    private GoodsFeign goodsFeign;

    //@Autowired
    private SpecificationFeign specificationFeign;
   // @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

   // @Resource
    private CategoryFeign categoryFeign;

   // @Resource
    private BrandFeign brandFeign;

   // @Override
    public Map<String, Object> getGoods(Integer spuId) {
        Map<String, Object> map = new HashMap<>();
        //spu deta
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        spuDTO.setSaleable(1);
        Result<List<SpuDTO>> spuResult = goodsFeign.getSpuInfo(spuDTO);
        if (spuResult.isSuccess()) {
            map.put("spu", spuResult.getData().get(0));
        }
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetail(spuId);
        if (spuDetailResult.isSuccess()){
            map.put("spuDetail",spuDetailResult.getData());
        }
        //sku and stock
        Result<List<SkuDTO>> skusList = goodsFeign.getSkus(spuId);
        if(skusList.isSuccess()){
            map.put("skusList",skusList.getData());
        }
        //category
        Result<List<CategoryEntity>> categoryByIds = categoryFeign.getCategoryByIds(spuResult.getData().get(0).getCid1()+","+spuResult.getData().get(0).getCid2()+","+spuResult.getData().get(0).getCid3());
        if(categoryByIds.isSuccess()){
            map.put("categoryList",categoryByIds.getData());
        }
        //brand
        Result<List<BrandEntity>> brandByIds = brandFeign.getBrandByIds(spuResult.getData().get(0).getBrandId() + "");
        if (brandByIds.isSuccess()){
            map.put("brand",brandByIds.getData().get(0));
        }
        //规格组
        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setCid(spuResult.getData().get(0).getCid3());
        Result<List<SpecGroupEntity>> sepcGroupInfo = specificationFeign.getSepcGroupInfo(specGroupDTO);
        if (sepcGroupInfo.isSuccess()) {
            List<SpecGroupDTO> groupParams = sepcGroupInfo.getData().stream().map(group -> {
                SpecGroupDTO groupDTO = BaseBean.copyProperties(group, SpecGroupDTO.class);
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setGroupId(group.getId());
                specParamDTO.setGeneric(true);
                Result<List<SpecParamEntity>> specParamInfo = specificationFeign.getSpecParamInfo(specParamDTO);
                if (specParamInfo.isSuccess()) {
                    groupDTO.setParams(specParamInfo.getData()); //通用参数
                }
                return groupDTO;
            }).collect(Collectors.toList());
            map.put("groupList",groupParams);

        }
        //特有参数
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuResult.getData().get(0).getCid3());
        specParamDTO.setGeneric(false);
        Result<List<SpecParamEntity>> specParamInfo = specificationFeign.getSpecParamInfo(specParamDTO);
        Map<Integer, String> SpecParamMap = new HashMap<>();
        if(specParamInfo.isSuccess()){
            specParamInfo.getData().forEach(param->SpecParamMap.put(param.getId(),param.getName()));
            map.put("specParamMap",SpecParamMap);
        }

        return map;
    }
}
