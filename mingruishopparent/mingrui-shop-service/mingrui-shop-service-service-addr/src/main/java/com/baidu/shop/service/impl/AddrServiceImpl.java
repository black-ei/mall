package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bai.shop.entity.AddrEntity;
import com.bai.shop.service.AddrService;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.mapper.AddrMapper;
import com.baidu.shop.utils.JwtUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class AddrServiceImpl extends BaseApiService implements AddrService {

    @Resource
    private AddrMapper addrMapper;

    @Resource
    private JwtConfig jwtConfig;

    @Override//设置默认地址
    public Result<List<AddrEntity>> theDefault(@NotNull Integer addrId, String token) {
        UserInfo userInfo = null;
        try {
            userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError("身份失效!!!");
        }
        List<AddrEntity> addrList = this.getAddrListByUserId(null,userInfo.getId());//获取用户所有地址信息
        UserInfo finalUserInfo = userInfo;
        addrList.stream().forEach(addr -> {
            addr.setAddrStatus(addr.getAddrId()==addrId?true:false);//将要修改的改为默认(true),其他改为不默认(fasle)
            this.editAddr(addr, finalUserInfo.getId());//根据userId和地址id将
        });

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delAddrs(@NotNull Integer addrId, String token) {
        UserInfo userInfo = null;
        try {
            userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError("身份失效!!!");
        }
        Example example = new Example(AddrEntity.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userInfo.getId());//为了保证其他用户的地址被误删
        criteria.andEqualTo("addrId",addrId);
        addrMapper.deleteByExample(example);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> editAddr(AddrEntity addrEntity, String token) {
        UserInfo userInfo = null;
        try {
            userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError("身份失效!!!");
        }
        this.editAddr(addrEntity,userInfo.getId());
        return this.setResultSuccess();
    }

    //根据userId和地址id修改地址信息
    private void editAddr(AddrEntity addrEntity,Integer userId){
        Example example = new Example(AddrEntity.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);//为了保证其他用户的地址被修改
        criteria.andEqualTo("addrId",addrEntity.getAddrId());
        addrMapper.updateByExampleSelective(addrEntity,example);
    }

    @Override
    public Result<List<AddrEntity>> getAddrs(Integer addrId,String token) {
        UserInfo userInfo = null;
        try {
            userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError("身份失效!!!");
        }
        return this.setResultSuccess(this.getAddrListByUserId(addrId,userInfo.getId()));
    }

    //通过用户Id获得用户所有地址信息
    private List<AddrEntity> getAddrListByUserId(Integer addrId,Integer userId){
        Example example = new Example(AddrEntity.class);//
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        if(addrId!=null) criteria.andEqualTo("addrId",addrId);
        example.orderBy("addrStatus").desc();
        return addrMapper.selectByExample(example);
    }

    @Override
    @Transactional
    public Result<JSONObject> saveAddr(AddrEntity addrEntity,String token) {
        UserInfo userInfo = null;
        try {
            userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            addrEntity.setUserId(userInfo.getId());//用户ID
            addrEntity.setAddrStatus(false);//false不为不默认
            addrMapper.insertSelective(addrEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError("身份失效!!!");
        }
        return this.setResultSuccess();
    }
}
