package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.BaseBean;
import com.baidu.shop.base.Result;
import com.baidu.shop.constant.MyPhoneConstant;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.UserService;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.LuosimaoDuanxinUtil;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Random;

@RestController
@Slf4j
public class UserServiceImpl extends BaseApiService implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public Result<JsonObject> sendValidCode(@NotEmpty String phone, @NotNull String code) {
        String checkCode = redisRepository.get(MyPhoneConstant.PHONE_PRE + phone);
        if (!code.equals(checkCode)) return this.setResultError("验证码不正确!");
        return this.setResultSuccess();
    }

    @Override
    public Result<JsonObject> sendValidCode(UserDTO userDTO) {
        if(userDTO.getPhone().length()!=11) return this.setResultError("手机号不正确!");
        String code = (int)((Math.random() * 9 + 1) * 100000)+"";//得到一个100000 - 999999 的随机数
        log.info("phone:{} ,code: {}",userDTO.getPhone(),code);

        //LuosimaoDuanxinUtil.SendCode(userDTO.getPhone(),code);//发送验证码
        redisRepository.set(MyPhoneConstant.PHONE_PRE+userDTO.getPhone(),code);//像redis中添加
        redisRepository.expire(MyPhoneConstant.PHONE_PRE +userDTO.getPhone(),60);//设置过期时间
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> register(UserDTO userDTO) {
        UserEntity userEntity = BaseBean.copyProperties(userDTO, UserEntity.class);

        Integer name = registerCheck(userDTO.getUsername(), 1).getData();
        Integer phone = registerCheck(userDTO.getPhone(), 2).getData();
        if(name!=0 || phone!=0) return this.setResultError("注册失败!");

        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(),BCryptUtil.gensalt()));//加密密码
        userEntity.setCreated(new Date());
        userMapper.insert(userEntity);

        return this.setResultSuccess();
    }

    @Override
    public Result<Integer> registerCheck(String value, Integer type) {
        Example example = new Example(UserEntity.class);
        example.createCriteria().andEqualTo(type == 1 ? "username":"phone", value);
        int i = userMapper.selectCountByExample(example);

        return this.setResultSuccess(i);
    }
}
