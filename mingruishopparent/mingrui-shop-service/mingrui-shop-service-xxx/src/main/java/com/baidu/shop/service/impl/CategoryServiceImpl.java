package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryBrand;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.status.HTTPStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {
    @Override
    public Result<List<CategoryEntity>> queryCategoryByBrandId(Integer brandId) {
        return this.setResultSuccess(categoryMapper.queryCategoryByBrandId(brandId));
    }

    @Autowired
    private CategoryMapper categoryMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByIds(String categoryStrId) {
        if (categoryStrId==null || "".equals(categoryStrId)) return this.setResultError("参数为空");

        List<CategoryEntity> list = categoryMapper
                .selectByIdList(Arrays.asList(categoryStrId.split(","))
                        .stream().map(id -> Integer.valueOf(id))
                        .collect(Collectors.toList()));
        return this.setResultSuccess(list);
    }

    @Override
    @Transactional
    public Result<JSONObject> saveCategory(CategoryEntity categoryEntity) {
        /*直接而将当前父节点的isParent修改为1*/
        CategoryEntity category = new CategoryEntity();
        category.setId(categoryEntity.getParentId());
        category.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(category);

        categoryMapper.insertSelective(categoryEntity);

        return this.setResultSuccess("新增成功!");
    }

    @Override
    @Transactional
    public Result<JSONObject> editCategoryById(@RequestBody CategoryEntity categoryEntity) {
        try {
            categoryMapper.updateByPrimaryKeySelective(categoryEntity);
        } catch (Exception e) {
            return this.setResultError(HTTPStatus.RUNTIME_ERROR,"修改失败!");
        }
        return this.setResultSuccess("修改成功!");
    }

    @Override
    @Transactional
    public Result<JSONObject> delCategoryById(Integer id) {

        //根据id查询当前节点信息
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);
        if(null==categoryEntity) return this.setResultError("数据不存在!");

        //判断当前节点是否为父节点
        if (categoryEntity.getIsParent()==1) return this.setResultError("当前节点为父节点,不能删除!");

         //判断是否有品牌关联分类表
        Example example1 = new Example(CategoryBrand.class);
        example1.createCriteria().andEqualTo("categoryId",id);
        List<CategoryBrand> categoryBrands = categoryBrandMapper.selectByExample(example1);
        if(categoryBrands.size()>0)return this.setResultError("当前节点有品牌绑定,请先删除品牌");

        //判断当前节点的父节点是否有其他子节点
        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId", categoryEntity.getParentId());
        List<CategoryEntity> entityList = categoryMapper.selectByExample(example);

        //当前节点的父节点只有当前节点,将当前节点的父节(删除后当前节点后就不是子节点而变成叶子节点)把isParent改为0
        if(entityList.size()<=1){
            CategoryEntity entity = new CategoryEntity();
            entity.setId(categoryEntity.getParentId());
            entity.setIsParent(0);
            categoryMapper.updateByPrimaryKeySelective(entity);
        }

        //根据id删除当前节点
        categoryMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess("删除成功!");
    }

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity entity = new CategoryEntity();
        entity.setParentId(pid);
        List<CategoryEntity> list = categoryMapper.select(entity);
        return this.setResultSuccess(list);
    }
}
