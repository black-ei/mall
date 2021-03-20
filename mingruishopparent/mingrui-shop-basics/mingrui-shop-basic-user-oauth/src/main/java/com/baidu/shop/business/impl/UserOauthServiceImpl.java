package com.baidu.shop.business.impl;

import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserOauthMapper;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.RsaUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserOauthServiceImpl implements UserOauthService {

    @Resource
    private UserOauthMapper userOauthMapper;


    @Override//登陆
    public String login(UserEntity userEntity,JwtConfig jwtConfig) {

        String token = null;
        //验证用户是否存在
        Example example = new Example(userEntity.getClass());
        example.createCriteria().andEqualTo("username",userEntity.getUsername());
        List<UserEntity> user = userOauthMapper.selectByExample(example);
        if (user.size()==1){
            //验证密码是正确
            if (BCryptUtil.checkpw(userEntity.getPassword(),user.get(0).getPassword())){
                //登陆成功 获得token
                try {
                    token = JwtUtils.generateToken(new UserInfo(user.get(0).getId(),user.get(0).getUsername()),
                            jwtConfig.getPrivateKey(),60);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return token;
    }
}
