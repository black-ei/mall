package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.BaseBean;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.shop.service.SpecificationService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;


    @Override
    public Result<SpecParamEntity> getSpecParamInfo(@NotNull Integer id) {
        return this.setResultSuccess(specParamMapper.selectByPrimaryKey(id));
    }

    @Override
    @Transactional
    public Result<JSONObject> deleteSpecParamInfo(@NotNull(message = "主键不能为空") Integer id) {
        specParamMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess("删除成功");
    }

    @Override
    @Transactional
    public Result<JSONObject> editSpecParamInfo(SpecParamDTO specParamDTO) {

        specParamMapper.updateByPrimaryKeySelective(BaseBean.copyProperties(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess("操作成功");
    }

    @Override
    @Transactional
    public Result<JSONObject> saveSpecParamInfo(SpecParamDTO specParamDTO) {
        specParamMapper.insertSelective(BaseBean.copyProperties(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess("操作成功");
    }

    @Override
    public Result<List<SpecParamEntity>> getSpecParamInfo(SpecParamDTO specParamDTO) {
        if(null==specParamDTO) return this.setResultError("参数不能为空");

        Example example = new Example(SpecParamEntity.class);
        Example.Criteria criteria = example.createCriteria();
        if(null!=specParamDTO.getGroupId()) criteria.andEqualTo("groupId",BaseBean.copyProperties(specParamDTO,SpecParamEntity.class).getGroupId());
        if(null!=specParamDTO.getCid()) criteria.andEqualTo("cid",BaseBean.copyProperties(specParamDTO,SpecParamEntity.class).getCid());
        if(null!=specParamDTO.getSearching()) criteria.andEqualTo("searching",BaseBean.copyProperties(specParamDTO,SpecParamEntity.class).getSearching());
        if(specParamDTO.getGeneric()!=null) criteria.andEqualTo("generic",BaseBean.copyProperties(specParamDTO,SpecParamEntity.class).getGeneric());

        return this.setResultSuccess(specParamMapper.selectByExample(example));

    }

    @Override
    @Transactional
    public Result<List<JsonObject>> saveSepcGroupInfo(SpecGroupDTO specGroupDTO) {
        specGroupMapper.insertSelective(BaseBean.copyProperties(specGroupDTO,SpecGroupEntity.class));
        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<List<JsonObject>> editSepcGroupInfo(SpecGroupDTO specGroupDTO) {
        specGroupMapper.updateByPrimaryKeySelective(BaseBean.copyProperties(specGroupDTO,SpecGroupEntity.class));
        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<List<JsonObject>> delSepcGroupInfo(@NotNull Integer id) {
        SpecParamEntity specParamEntity = new SpecParamEntity();
        specParamEntity.setGroupId(id);
        int i = specParamMapper.selectCount(specParamEntity);
        if(i>0)return this.setResultError("当前规格下有绑定参数,清先删除参数!");
        specGroupMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess("删除成功");
    }

    @Override
    public Result<List<SpecGroupEntity>> getSepcGroupInfo(SpecGroupDTO specGroupDTO) {
        Example example = new Example(SpecGroupEntity.class);
        example.createCriteria().andEqualTo("cid", BaseBean.copyProperties(specGroupDTO,SpecGroupEntity.class).getCid());
        return this.setResultSuccess(specGroupMapper.selectByExample(example));
    }
}
