package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.BaseBean;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrand;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    @Transactional
    public Result<JSONObject> delete(@NotNull(message = "id不能为空") Integer id) {
        brandMapper.deleteByPrimaryKey(id);
        this.deleteBrandCategory(id);
        return this.setResultSuccess("删除成功!");
    }

    @Override
    @Transactional
    public Result<JSONObject> edit(BrandDTO brandDTO) {
        //克隆
        BrandEntity brandEntity = BaseBean.copyProperties(brandDTO, BrandEntity.class);
        //重新给品牌首字母赋值
        brandDTO.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0)),false).charAt(0));
        brandMapper.updateByPrimaryKeySelective(brandEntity);

        //根据brandId删除中间表相关数据
        this.deleteBrandCategory(brandEntity.getId());
        //重新给添加分类信息
        this.inssertCategoryBrand(brandDTO.getCategories(),brandEntity.getId());
        return this.setResultSuccess("修改成功");
    }

    @Autowired
    public void setBrandMapper(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    @Override
    @Transactional
    public Result<JSONObject> save(BrandDTO brandDTO) {

        BrandEntity brandEntity = BaseBean.copyProperties(brandDTO, BrandEntity.class);
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0)),false).charAt(0));

        brandMapper.insertSelective(brandEntity);
        //维护中间表
        this.inssertCategoryBrand(brandDTO.getCategories(),brandEntity.getId());

        return this.setResultSuccess("新增成功!");
    }

    @Override
    public Result<PageInfo<BrandEntity>> page(BrandDTO brandDTO) {

        //pagehelper分页
        if(brandDTO.getRows()!=-1) PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

        //克隆
        BrandEntity brandEntity = BaseBean.copyProperties(brandDTO, BrandEntity.class);
        Example example = new Example(BrandEntity.class);
        //排序
        if(!StringUtils.isEmpty(brandDTO.getOrder())) example.setOrderByClause(brandDTO.getOrderByClause());
        //条件查询
        if(!StringUtils.isEmpty(brandEntity.getName())) example.createCriteria().andLike("name","%"+brandEntity.getName()+"%");

        List list = brandMapper.selectByExample(example);
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);
        return this.setResultSuccess(pageInfo);
    }

    private void deleteBrandCategory(Integer id){
        Example example = new Example(CategoryBrand.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }

    private void inssertCategoryBrand(String categorys,Integer brandId){

        if(categorys.contains(",")){
            categoryBrandMapper.insertList(Arrays.asList(categorys.split(","))
                    .parallelStream()
                    .map(str -> new CategoryBrand(Integer.valueOf(str),brandId))
                    .collect(Collectors.toList()));
        }else {
            categoryBrandMapper.insertSelective(new CategoryBrand(Integer.valueOf(categorys),brandId));
        }
    }
}
