package com.baidu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.feign.BrandFeign;
import com.baidu.feign.CategoryFeign;
import com.baidu.feign.GoodsFeign;
import com.baidu.feign.SpecificationFeign;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.BaseBean;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    @Autowired
    private GoodsFeign goodsFeign;
    @Autowired
    private SpecificationFeign specificationFeign;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private CategoryFeign categoryFeign;
    @Autowired
    private BrandFeign brandFeign;

    @Value(value = "${mrshop.static.html.path}")
    private String staticHtmlPath;

    @Autowired
    private TemplateEngine templateEngine;


    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {
        Map<String, Object> goodsMap = this.getGoods(spuId);
        Context context = new Context();
        context.setVariables(goodsMap);//创建模板引擎上下文
        File file = new File(staticHtmlPath,spuId+".html");
        System.out.println(file.exists());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter printWriter =null;
        try {
            printWriter = new PrintWriter(file,"UTF-8");//构建输出流
            //根据模板生成静态文件
            //param1:模板名称 params2:模板上下文[上下文中包含了需要填充的数据],文件输出流
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            if(printWriter!=null){
                try {
                    printWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {
        goodsFeign.getSpuInfo(new SpuDTO()).getData().forEach(spu->this.createStaticHTMLTemplate(spu.getId()));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteStaticHTMLTemplate(Integer spuId) {
        File file = new File(staticHtmlPath,spuId+".html");
        if (file.exists()) {
            file.delete();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearStaticHTMLTemplate() {
        goodsFeign.getSpuInfo(new SpuDTO()).getData().forEach(spu->this.deleteStaticHTMLTemplate(spu.getId()));
        return this.setResultSuccess();
    }

    private Map<String, Object> getGoods(Integer spuId) {
        Map<String, Object> map = new HashMap<>();
        SpuDTO spu = this.getSpu(spuId);
        //sku信息
        map.put("spu",spu);
        //spuDetail信息
        map.put("spuDetail",this.getSpuDetail(spuId));
        //sku信息
        map.put("skusList",this.getSkusList(spuId));
        //分类信息
        map.put("categoryList",this.getCategoryList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        //品牌信息
        map.put("brand",this.getBrand(spu.getBrandId()));
        //规格组 以及通用参数
        map.put("groupList",this.getGroupList(spu.getCid3()));//分类Id);
        //特有参数
        map.put("specParamMap",this.getSpecParamMap(spu.getCid3()));
        return map;
    }

    private SpuDTO getSpu(Integer spuId){
        //spu deta
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        spuDTO.setSaleable(1);
        SpuDTO spu = null;
        Result<List<SpuDTO>> spuResult = goodsFeign.getSpuInfo(spuDTO);
        if (spuResult.isSuccess()) {
            spu = spuResult.getData().get(0);
        }
        return spu;
    }

    private SpuDetailEntity getSpuDetail(Integer spuId){
        SpuDetailEntity spuDetail = null;
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetail(spuId);
        if (spuDetailResult.isSuccess()){
           spuDetail = spuDetailResult.getData();
        }
        return spuDetail;
    }

    private List<SkuDTO> getSkusList(Integer spuId){
        //sku and stock
        List<SkuDTO> skus = null;
        Result<List<SkuDTO>> skusList = goodsFeign.getSkus(spuId);
        if(skusList.isSuccess()){
            skus = skusList.getData();
        }
        return skus;
    }

    private List<CategoryEntity> getCategoryList(Integer cid1,Integer cid2,Integer cid3){
        List<CategoryEntity> categoryByIds = null;
        Result<List<CategoryEntity>> category = categoryFeign.getCategoryByIds(cid1+","+cid2+","+cid3);
        if(category.isSuccess()){
            categoryByIds = category.getData();
        }
        return categoryByIds;
    }

    private BrandEntity getBrand(Integer brandId){
        //brand
        Result<List<BrandEntity>> brandByIds = brandFeign.getBrandByIds(brandId + "");
        BrandEntity brandEntity = null;
        if (brandByIds.isSuccess()){
            brandEntity= brandByIds.getData().get(0);
        }
        return brandEntity;
    }

    private List<SpecGroupDTO> getGroupList(Integer categoryId){

        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setCid(categoryId);
        Result<List<SpecGroupEntity>> sepcGroupInfo = specificationFeign.getSepcGroupInfo(specGroupDTO);
        List<SpecGroupDTO> groupParams = new ArrayList<>();
        if (sepcGroupInfo.isSuccess()) {
            groupParams = sepcGroupInfo.getData().stream().map(group -> {
                SpecGroupDTO groupDTO = BaseBean.copyProperties(group, SpecGroupDTO.class);//转成DTO 在DTO中加入List <SpecParamEntity>
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setGroupId(group.getId());
                specParamDTO.setGeneric(true);
                Result<List<SpecParamEntity>> specParamInfo = specificationFeign.getSpecParamInfo(specParamDTO);//查出规格组下的通用参数封装到list
                if (specParamInfo.isSuccess()) {
                    groupDTO.setParams(specParamInfo.getData()); //通用参数List
                }
                return groupDTO;
            }).collect(Collectors.toList());
        }
        return groupParams;
    }

    private Map<Integer, String>  getSpecParamMap(Integer categoryId){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(categoryId);
        specParamDTO.setGeneric(false);
        Result<List<SpecParamEntity>> specParamInfo = specificationFeign.getSpecParamInfo(specParamDTO);
        Map<Integer, String> specParamMap = new HashMap<>();
        if(specParamInfo.isSuccess()){//将特有规格封装成 id : name map 前台好处理
            specParamInfo.getData().forEach(param->specParamMap.put(param.getId(),param.getName()));
        }
        return specParamMap;
    }

}
