package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ESHighLightUtil;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.StringUtils;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@RestController
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override//es搜索
    public Result<List<GoodsDoc>> searchInfo(String search,Integer page) {
        if (search==null || "".equals(search)) return this.setResultError("参数不能为空");
        if(page<1 || page==null)  return this.setResultError("page参数不能正确");
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //多字段查询
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","category"));
        queryBuilder.withPageable(PageRequest.of(page,2));//分页
        //设置高亮
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilderBuilder("title"));
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]
                {"id","title","skus","price"}, null));//设置查询出来的内容,页面上做多只需要id,title,skus

        SearchHits<GoodsDoc> highLightHit = ESHighLightUtil.getHighLightHit(elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class));
        Long total = highLightHit.getTotalHits();//总条数
        Double pageTotal = Math.ceil(total.doubleValue() / 2);//总页数
        Map<String, Long> pageAndPageTotalMap = new HashMap<>();
        pageAndPageTotalMap.put("total",total);
        pageAndPageTotalMap.put("pageTotal",pageTotal.longValue());

        List<GoodsDoc> docList = highLightHit.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());

        return this.setResult(HTTPStatus.OK,JSONUtil.toJsonString(pageAndPageTotalMap),docList);
    }

    @Override//mysql同步es
    public Result<JSONObject> initGoodsEsData() {
        IndexOperations index = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (!index.exists()) {
            index.create();//创建索引
            index.createMapping();//创建映射
        }

        elasticsearchRestTemplate.save(this.esGoodsInfo());//获取数据批量新增

        return this.setResultSuccess();
    }

    @Override//删除es商品索引
    public Result<JSONObject> clearGoodsEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }
        return this.setResultSuccess();
    }

    //@Override
    private List<GoodsDoc> esGoodsInfo() {
        SpuDTO spuDTO = new SpuDTO();
        // spuDTO.setPage(1);
        //spuDTO.setRows(5);
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);
        if (spuInfo.isSuccess()){//判断请求是否成功
            return spuInfo.getData().stream().map(spu -> {
                GoodsDoc goodsDoc = new GoodsDoc();
                //用于搜索
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                //spu信息填充
                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setCreateTime(spu.getCreateTime());
                //sku数据填充
                Map<ArrayList<Long>, List<Map<String, Object>>> arrayListListMap = this.getskusAndprice(spu.getId());
                arrayListListMap.forEach((k,y)-> {
                    goodsDoc.setPrice(k);
                    goodsDoc.setSkus(JSONUtil.toJsonString(y));
                });
                goodsDoc.setSpecs(this.getSpecsMap(spu));//规格参数填充
                return goodsDoc;
            }).collect(Collectors.toList());
        }
        return null;
    }

    //通过spuId来获取sku信息
    private Map<ArrayList<Long>, List<Map<String, Object>>> getskusAndprice(Integer spuId){
        Result<List<SkuDTO>> skusInfo = goodsFeign.getSkus(spuId);
        Map<ArrayList<Long>, List<Map<String, Object>>> priceListAndskusListMap = null;
        if (skusInfo.isSuccess()) {//判断请求是否成功
            ArrayList<Long> priceList = new ArrayList<>();
            List<Map<String, Object>> skuListMap = skusInfo.getData().stream().map(sku -> {
                Map<String, Object> skuHashMap = new HashMap<>();
                skuHashMap.put("id", sku.getId());
                skuHashMap.put("title", sku.getTitle());
                skuHashMap.put("image", sku.getImages());
                skuHashMap.put("spuId", sku.getSpuId());

                priceList.add(sku.getPrice().longValue());
                return skuHashMap;
            }).collect(Collectors.toList());
            priceListAndskusListMap = new HashMap<>();
            priceListAndskusListMap.put(priceList,skuListMap);//放入map返回
        }
        return priceListAndskusListMap;
    }

    //获取规格参数信息
    private Map<String, Object> getSpecsMap(SpuDTO spu){
        SpecParamDTO dto = new SpecParamDTO();
        dto.setCid(spu.getCid3());
        dto.setSearching(true);//只查询 参与查询字段
        Map<String, Object> specsHashMap = null;
        Result<List<SpecParamEntity>> specParamInfo = specificationFeign.getSpecParamInfo(dto);//获取参数信息
        if (specParamInfo.isSuccess()) {
            Result<SpuDetailEntity> spuDetail = goodsFeign.getSpuDetail(spu.getId());//获取参数值
            if (spuDetail.isSuccess()) {
                specsHashMap = this.getSpecsMap(specParamInfo, spuDetail);
            };
        }
        return specsHashMap;
    }

    //将规格参数信息 转换为map返回
    private Map<String, Object> getSpecsMap(Result<List<SpecParamEntity>> specParamInfo,Result<SpuDetailEntity> spuDetail){
        Map<String, Object> specsHashMap = new HashMap<>();
        Map<String, String> genericMap = JSONUtil.toMapValueString(spuDetail.getData().getGenericSpec());//将通用字段json转map
        Map<String, List<String>> genericListMap = JSONUtil.toMapValueStrList(spuDetail.getData().getSpecialSpec());//将特有字段json转map<String,List>

        specParamInfo.getData().forEach(param -> {
            if (param.getGeneric()) {//通用规格参数
                specsHashMap.put(
                        param.getName(),
                        (param.getNumeric() && StringUtils.isEmpty(param.getSegments()))//为数字且有范围信息就转为区间
                                ? chooseSegment(genericMap.get(param.getId() + ""), param.getSegments(), null == param.getUnit() ? "" : param.getUnit())
                                : genericMap.get(param.getId() + ""));
            } else {//特有规格参数
                specsHashMap.put(param.getName(), genericListMap.get(param.getId() + ""));
            }
        });

        return specsHashMap;
    }

    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
        // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
        // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }
}
