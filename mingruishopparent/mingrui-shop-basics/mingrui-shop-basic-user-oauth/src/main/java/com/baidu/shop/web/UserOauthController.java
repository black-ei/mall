package com.baidu.shop.web;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "oauth")
@Api(tags = "认证中心")
public class UserOauthController extends BaseApiService {

    @Autowired
    private UserOauthService userOauthService;

    @Resource
    private JwtConfig jwtConfig;

    @ApiOperation(value = "验证是否登录")
    @GetMapping(value = "verifyCookie")
    public Result<JSONObject> verifyCookie(@CookieValue(value = "MRSHOP_TOKEN") String token,HttpServletRequest request,HttpServletResponse response){
        UserInfo infoFromToken = null;
        try {
            infoFromToken = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //如果上行代码不报错,就证明token有效,进行token的刷新
            String newToken = JwtUtils.generateToken(infoFromToken, jwtConfig.getPrivateKey(), jwtConfig.getExpire());//重新获得cookie
            CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),newToken,jwtConfig.getCookieMaxAge(),true);//写入cookie
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError(HTTPStatus.VERIFY_COOKIE_ERROR,"未登录!");
        }
        return this.setResultSuccess(infoFromToken);
    }


    @ApiOperation(value = "登陆")
    @PostMapping(value = "login")
    public Result<JSONObject> login(@RequestBody @Validated UserEntity userEntity, HttpServletRequest request, HttpServletResponse response){
        //登陆成功返回token,否者null;
            String token = userOauthService.login(userEntity,jwtConfig);
            if (token==null) return this.setResultError("用户名或密码输入错误!");
        //将token 放入cookie
        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);


        return this.setResultSuccess();
    }
}
