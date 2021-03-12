package com.baidu.shop.status;

public class HTTPStatus {
    public static final int OK = 200;//成功
    public static final int ERROR = 500;//失败
    public static final int RUNTIME_ERROR = 5001;//失败
    public static final int PARAMS_VALIDATE_ERROR = 5002;//参数校验失败
    public static final int VERIFY_COOKIE_ERROR = 403;//用户未登录拒绝访问
}

