package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.response.GoodsResponse;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ESHighLightUtil;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.StringUtils;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.*;
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

    @Resource
    private CategoryFeign categoryFeign;

    @Resource
    private BrandFeign brandFeign;

    @Override
    public Result<JSONObject> saveData(Integer spuId) {//通过spuId新增es库商品信息
         elasticsearchRestTemplate.save(this.esGoodsInfo(spuId).get(0));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delData(Integer spuId) {//通过spu删除es库商品信息
        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());

        elasticsearchRestTemplate.delete(goodsDoc);
        return this.setResultSuccess();
    }

    @Override//es搜索
    public Result<List<GoodsDoc>> searchInfo(String search,Integer page,String filter) {
        if (search==null || "".equals(search)) return this.setResultError("参数不能为空");
        if(page<1 || page==null)  return this.setResultError("page参数不能正确");

        NativeSearchQueryBuilder builder = this.getNativeSearchQueryBuilder(search, page,filter);//获得builder
        SearchHits<GoodsDoc> highLightHit = ESHighLightUtil.getHighLightHit(elasticsearchRestTemplate.search(builder.build(), GoodsDoc.class));//查询es

        Long total = highLightHit.getTotalHits();//总条数
        Double pageTotal = Math.ceil(total.doubleValue() / 5);//总页数

        Aggregations aggregations = highLightHit.getAggregations();//获得聚合
        List<List> list = this.getHotIdAndCategoryAndBrand(aggregations);////获得热点cid ategory信息 brand信息
        //获取GoodsDoc数据
        List<GoodsDoc> docList = highLightHit.getSearchHits().stream().map(hit -> hit.getContent()).collect(Collectors.toList());

        return new GoodsResponse(total.intValue(),pageTotal.intValue(),list.get(1),list.get(2),docList,this.getParamMapByCid((Integer) list.get(0).get(0), search));
    }

    //构造NativeSearchQueryBuilder
    private NativeSearchQueryBuilder getNativeSearchQueryBuilder(String search,Integer page,String filter){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //多字段查询
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","category"));
        //聚合 分桶
        queryBuilder.addAggregation(AggregationBuilders.terms("agg_category").field("cid3"));//通过分类id分桶
        queryBuilder.addAggregation(AggregationBuilders.terms("agg_branId").field("brandId"));//通过分类id分桶
        //设置高亮
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilderBuilder("title"));
        //二次(用户选择)过滤信息
        if(!Strings.isEmpty(filter) && filter.length()>2){
            Map<String, String> filterMap = JSONUtil.toMapValueString(filter);
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            filterMap.forEach((k,v)->{
                boolQueryBuilder.must(QueryBuilders.matchQuery((k.equals("cid3") || k.equals("brandId"))?k:"specs."+k+".keyword",v));
            });
            queryBuilder.withFilter(boolQueryBuilder);
        }


        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","title","skus"}, null));//过滤,页面上做多只需要id,title,skus
        queryBuilder.withPageable(PageRequest.of(page-1,5));//分页
        return queryBuilder;
    }

    //获得热点cid and category信息 and brand信息
    private List<List> getHotIdAndCategoryAndBrand(Aggregations aggregations){
        Terms agg_category = aggregations.get("agg_category");
        Terms agg_branId = aggregations.get("agg_branId");

        List<Integer> hotId = Arrays.asList(0);//聚合后最多商品id
        List<Long> countMax = Arrays.asList(0L);

        String categoryStrId = agg_category.getBuckets().stream().map(bucket -> {
            if (((Terms.Bucket) bucket).getDocCount()>countMax.get(0)){
                hotId.set(0,((Terms.Bucket) bucket).getKeyAsNumber().intValue());
                countMax.set(0,((Terms.Bucket) bucket).getDocCount());
            }
            return  ((Terms.Bucket) bucket).getKey().toString();
        }).collect(Collectors.joining(","));

        String brandStrId = agg_branId.getBuckets().stream().map(bucket -> ((Terms.Bucket) bucket).getKey().toString()).collect(Collectors.joining(","));
        Result<List<CategoryEntity>> resultCategory = categoryFeign.getCategoryByIds(categoryStrId);//获取分类信息
        Result<List<BrandEntity>> resultBrand = brandFeign.getBrandByIds(brandStrId);//获取品牌信息
        return Arrays.asList(hotId,resultCategory.isSuccess()?resultCategory.getData():null,resultBrand.isSuccess()?resultBrand.getData():null);
    }

    //获取规格参数
    private Map<String, List<String>> getParamMapByCid(Integer cid,String search){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(cid);
        specParamDTO.setSearching(true);
        Result<List<SpecParamEntity>> result = specificationFeign.getSpecParamInfo(specParamDTO);
        Map<String, List<String>> paramsMap =  new HashMap<>();
        if(result.isSuccess()){
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","category"));
            queryBuilder.withPageable(PageRequest.of(0,1));

            result.getData().forEach(params->queryBuilder.addAggregation(AggregationBuilders.terms(params.getName()).field("specs."+params.getName()+".keyword")));

            result.getData().forEach(params->{
                Terms aggregation = elasticsearchRestTemplate
                        .search(queryBuilder.build(), GoodsDoc.class)
                        .getAggregations().get(params.getName());
                paramsMap.put(params.getName(),aggregation.getBuckets().stream().map(bucke ->bucke.getKeyAsString()).collect(Collectors.toList()));
            });
        }
        return paramsMap;
    }

    @Override//mysql同步es
    public Result<JSONObject> initGoodsEsData() {
        IndexOperations index = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (!index.exists()) {
            index.create();//创建索引
            index.createMapping();//创建映射
        }

        elasticsearchRestTemplate.save(this.esGoodsInfo(null));//获取数据批量新增

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
    private List<GoodsDoc> esGoodsInfo(Integer spuId) {
        SpuDTO spuDTO= new SpuDTO();
        spuDTO.setId(spuId);
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
                goodsDoc.setBrandId(spu.getBrandId().longValue());
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
                skuHashMap.put("price", sku.getPrice());

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
