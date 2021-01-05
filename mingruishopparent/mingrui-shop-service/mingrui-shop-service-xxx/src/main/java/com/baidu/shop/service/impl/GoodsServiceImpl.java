package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandMapper brandMapper;

    @Override
    public Result<PageInfo<SpuEntity>> getSpuInfo(SpuDTO spuDTO) {
        //分页插件
        if(spuDTO.getPage()!=null && spuDTO.getRows()!=null && spuDTO.getRows()>0) PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());

        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();
      if(null!=spuDTO){
          //判断 上架 下架
          if (spuDTO.getSaleable()!=null && spuDTO.getSaleable()<2 && spuDTO.getSaleable()>=0) criteria.andEqualTo("saleable",spuDTO.getSaleable());

          //模糊匹配
          if(spuDTO.getTitle()!=null && !"".equals(spuDTO.getTitle()))
              criteria.andLike("title","%"+spuDTO.getTitle()+"%");
          //排序
          if(!StringUtils.isEmpty(spuDTO.getSort())) example.setOrderByClause(spuDTO.getOrderByClause());
      }

        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);

        PageInfo<SpuEntity> pageInfo = new PageInfo<>(spuEntities);
        return this.setResultSuccess(pageInfo);
    }
}
