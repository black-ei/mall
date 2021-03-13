package com.baidu.filter;

import com.baidu.config.JwtConfig;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.RequestContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class LoginFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public boolean shouldFilter() {//放掉不需要验证的验证 比如搜索 登陆 注册请求
        RequestContext requestContext = RequestContext.getCurrentContext();//获取上下文
        HttpServletRequest request = requestContext.getRequest();//获取request
        String requestURI = request.getRequestURI();//获取当前uri
        List<Boolean> booleans = Arrays.asList(true);

        jwtConfig.getExcludePath().forEach(uri->{
            if (requestURI.equals(uri) || requestURI.contains(uri)) {
                booleans.set(0,false);
            }
        });
        log.info("uri:"+requestURI);

        return booleans.get(0); //返回true 将执行run函数
    }

    @Override
    public Object run() throws ZuulException {
        //因为微服务的各个服务之间没有关联  而且由zull来转发 所以用zull来做是否登陆的全局验证
        //我们token在cookie中,首先获取cookie
        RequestContext requestContext = RequestContext.getCurrentContext();//获取上下文
        HttpServletRequest request = requestContext.getRequest();//获取request
        String token = CookieUtils.getCookieValue(request, jwtConfig.getCookieName(), true);//获得token并解码
        try {
            JwtUtils.getInfoFromToken(token,jwtConfig.getPublicKey());//校验token
        } catch (Exception e) {
            log.info("校验失败 拦截:"+token);
            requestContext.setSendZuulResponse(false);//拦截请求
            requestContext.setResponseStatusCode(403);//返回response状态
            e.printStackTrace();
        }
        return null;//返回null将可以访问
    }
}
