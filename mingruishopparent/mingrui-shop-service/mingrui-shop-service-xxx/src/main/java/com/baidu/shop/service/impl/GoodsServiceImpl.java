package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.BaseBean;
import com.baidu.shop.base.Result;
import com.baidu.shop.component.MyRabbitMQ;
import com.baidu.shop.constant.MqMessageConstant;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.entity.StockEntity;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.javafx.util.Logging;
import lombok.extern.flogger.Flogger;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import sun.rmi.runtime.Log;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController

public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;
    @Resource
    private BrandMapper brandMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SpuDetailMapper spuDetailMapper;
    @Resource
    private SkuMapper skuMapper;
    @Resource
    private StockMapper stockMapper;
    @Resource
    private MyRabbitMQ myRabbitMQ;
/*
* 更新商品库存
* @Param1 skuId;
* @Param2 要减少OR增加的库存数量
* @Param2 1 为增加库存 其他为减少库存
* */
    @Override
    @Transactional
    public Result<JSONObject> updateStock(Long skuId, Integer reduce,Integer is) {
        try {
            StockEntity stock = stockMapper.selectByPrimaryKey(skuId);
            if (is==1){
                stock.setStock(stock.getStock()+reduce);
            }else{
                if (stock.getStock()<reduce)return this.setResultError("库存不足!");
                stock.setStock(stock.getStock()-reduce);
            }
            stockMapper.updateByPrimaryKeySelective(stock);
        } catch (Exception e) {
            e.printStackTrace();
            this.setResultError("啥也不是!!!");
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<SkuDTO> getSku(@NotNull Long skuId) {

        return this.setResultSuccess(skuMapper.selectSkuAndStock(skuId));
    }

    @Override
    @Transactional
    public Result<List<SkuDTO>> downGood(@NotNull Integer spuId) {
        //saleable
        SpuEntity spuEntity = spuMapper.selectByPrimaryKey(spuId);
        spuEntity.setSaleable(spuEntity.getSaleable()==1?0:1);
        spuMapper.updateByPrimaryKeySelective(spuEntity);
        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> delSpuInfo(@NotNull(message = "id不能为空") Integer spuId) {
        spuMapper.deleteByPrimaryKey(spuId);
        spuDetailMapper.deleteByPrimaryKey(spuId);
        //直接删除就无法删除stock,先查询查询sku,获得skuID
        this.delSkuAndStock(spuId);
        myRabbitMQ.send(spuId+"",MqMessageConstant.SPU_ROUT_KEY_DELETE);//发送MQ消息,删除es 信息
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> editSpuInfo(SpuDTO spuDTO) {
        //事务拆分 为了确保spuInfo提交之后才发送MQ消息,否者有可能发送了消息(MQ消费者消费时当前事务还未提交,就会查到未提交的脏数据)
            this.editSpuTran(spuDTO);
            myRabbitMQ.send(spuDTO.getId()+"",MqMessageConstant.SPU_ROUT_KEY_UPDATE);//发送MQ消息,修改es 信息
        return this.setResultSuccess();
    }

    @Transactional
    public void editSpuTran(SpuDTO spuDTO){
        SpuEntity spuEntity = BaseBean.copyProperties(spuDTO, SpuEntity.class);
        final Date date = new Date();
        //修改spu
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);
        //修改spuDetail
        spuDetailMapper.updateByPrimaryKeySelective(BaseBean.copyProperties(spuDTO.getSpuDetail(),SpuDetailEntity.class));
        //如果新增特有规格前台(笛卡尔积)会生成很多不同的版本所以(没有skuId)先删除原有的sku去,再将现在的sku新增进
        //删除sku和stock
        delSkuAndStock(spuEntity.getId());
        //进行新增sku和stock
        saveSkuAndStock(spuDTO,spuEntity.getId(),date);
    }

    @Override
    public Result<List<SkuDTO>> getSkus(@NotNull Integer spuId) {
        List<SkuDTO> list = skuMapper.getSkuAndStock(spuId);
        return this.setResultSuccess(list);
    }

    @Override
    public Result<SpuDetailEntity> getSpuDetail(Integer spuId) {
        return this.setResultSuccess(spuDetailMapper.selectByPrimaryKey(spuId));
    }

    @Override
    public Result<JSONObject> saveSpuInfo(SpuDTO spuDTO) {

        Integer spuId = saveSpuTran(spuDTO);//事务拆分 为了确保spuInfo提交之后才发送MQ消息,否者有可能发送了消息(MQ消费者消费时当前事务还未提交,就会查不到数据)

        myRabbitMQ.send(spuId+"", MqMessageConstant.SPU_ROUT_KEY_SAVE);//发送MQ消息,添加到es
        return this.setResultSuccess();
    }

    @Transactional
    public Integer saveSpuTran(SpuDTO spuDTO){
        final Date date = new Date();
        //新增spu
        SpuEntity spuEntity = BaseBean.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);
        //通过返回的spuid新增spudetail
        SpuDetailEntity spuDetailEntity = BaseBean.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);
        //进行新增sku和stock
        saveSkuAndStock(spuDTO,spuEntity.getId(),date);
        return spuEntity.getId();
    }


    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {
        //分页插件
        if(spuDTO.getPage()!=null && spuDTO.getRows()!=null && spuDTO.getRows()>0) PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());

        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();
      if(null!=spuDTO){
          //通过id查找spu
           if (spuDTO.getId()!=null) criteria.andEqualTo("id",spuDTO.getId());
          //判断 上架 下架
          if (spuDTO.getSaleable()!=null && spuDTO.getSaleable()<2 && spuDTO.getSaleable()>=0) criteria.andEqualTo("saleable",spuDTO.getSaleable());
          //模糊匹配
          if(spuDTO.getTitle()!=null && !"".equals(spuDTO.getTitle()))criteria.andLike("title","%"+spuDTO.getTitle()+"%");
          //排序
          if(StringUtils.isEmpty(spuDTO.getSort())) example.setOrderByClause(spuDTO.getOrderByClause());
      }
        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);

        //查询分类名称 和 品牌名称封装到supDTO返回集合
        List<SpuDTO> spuDTOList = spuEntities.stream().map(spuEntity -> {
            //克隆bean 将
            SpuDTO dto = BaseBean.copyProperties(spuEntity, SpuDTO.class);
            //通过cid...查询品牌分类名称
            dto.setCategoryName(categoryMapper.selectByIdList(Arrays.asList(dto.getCid1(), dto.getCid2(), dto.getCid3())).stream().map(category -> category.getName()).collect(Collectors.joining("/")));
            //根据spuBrandId查询brandName
            dto.setBrandName(brandMapper.selectByPrimaryKey(dto.getBrandId()).getName());
            return dto;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> pageInfo = new PageInfo<>(spuEntities);
        return this.setResult(HTTPStatus.OK,pageInfo.getTotal()+"",spuDTOList);
    }

    /*
    通过spuId删除sku和stock
    * */
    private void delSkuAndStock(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        //通过spuId查询skuId
        List<Long> collect = skuMapper.selectByExample(example).stream().map(sku -> sku.getId()).collect(Collectors.toList());
        //删除sku
        skuMapper.deleteByIdList(collect);
        //删除stock,(sku主键也是stock的主键)
        stockMapper.deleteByIdList(collect);
    }

    /*
     * 进行新增sku和stock*/
    private void saveSkuAndStock(SpuDTO spuDTO,Integer spuId,Date date){
        //新增sku,因为是一对多可能会有多个不同商品,所以是list
        spuDTO.getSkus().stream().forEach(skuDTO -> {
            SkuEntity skuEntity = BaseBean.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);
            //通过返回的skuID新增Stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }
}
