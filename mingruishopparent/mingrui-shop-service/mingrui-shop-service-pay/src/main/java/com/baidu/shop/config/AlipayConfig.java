package com.baidu.shop.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
public class AlipayConfig {
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016110200786786";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCjCK34zepWAZAtrt/AIGIbhvsUDDiqZONinZkTrnoo2kqV+KXUmMXRRVJgXTD29O8EWHq0bAQPgsm64fHlsNDqYXrHSi2A02hv3JrJrl9cAsUxW4KWSV2cmk49LA+tXtmw7RCCu22bKyLgKzj7+fLPXns1Oa6PQce2A+TMJHfs7S5pKM70Br8QUQxNOaVwss+m9ppYRKs4tvoGqg0mYZ1jY5Hi4GRgCvVHS632TWA/YRyMtpYlChNfK6xZS1DCxU2TVWIozwFMA5vlHdM5pwwVeGytPaVR1UnzwYjjQifdrFzUiL785r+w3QAGXDzk5O38PFvVeJ6g+OStEr60wAi3AgMBAAECggEAPr8Nbe+aW+WYHuzp8qDEAYIuyeV2PaY8mWy8SIXjQCvbfuLGuA7gO42piar0OGfGQe7nst7LpUiZ6VIiblth3IMN5PuYjbdPv5WxASQaUWhDip6EoOC1pZsBt6tokTorIFvs+dO2+HxenNYlxVzKZaMgWjjMBRi1C0luWl9CxK1Y/v5eK759YsS6kGnN1AtWrhB5eWrhKb+HbwAD2Yl7qnRbU2d4FXwYhoSvdLs5+kgRJSkJ6e8pXtAvXU88uYH89CfJq3gwFl1xbxurBysBdxGvUTX6yi5aa8R/4qU8BZMXvnRICnCPTh1r7jYIb2A8yDA59r8JwmkTpEjpwwMaMQKBgQDuwOpVfkKB116zfxY1QYzfe7s8Gs/6pgfPlcpgs/rqVhJXwCPDEhGGn3PiYtef3wlom9NpVRsJ1dWi9ZcLFKUB6f1o4JryRkXCG+s6M6pX0W5i6I6ReXcTMqet+b/r96yLqyF7mv35pn0iB/Dyyjz4tiLnGf41/15KW1HxcyJSzwKBgQCuz4oBTzuDrWu4kvvNVmeWdCJLITTNmBY97sNeR71VTVHRZ+CIsU4rh+5Ol1xPSSvAWfkeELonytMwHWSJoimwdFNRIhqMI7KL17NAQgAmB8fCGp3NmV5rwOtK+yhNjOAq8tG8eoQAi5xEJEwaj1PZDdIQJsIMdZuOa6Yg5hmFmQKBgAhKoti9PcoMlLlfHIJU+HN6dJItzDNEONk4toKNPZWNz61Eevl3Uyx9tSSTkjF3KXh8kOcDkaDR0E2MgeZcnS5n1ekig78OObX4Cj9HNA9Asri0ot7ifQQpqjEcj1G+lDt1ggAs7c0Rvx7Db9a+t0db6/JisLVnXPrwGPRtry95AoGBAKejXzTiRrCAHk1ipxbDRZOCy2BA9+IxdyLtVW2UXnzvKQcoFqr/XXdhY6b6gAuIn+xWXA/4+owudVgu17slR9VvuHaVvd+mXBK/R6bXpEzUKTx7Sdlguqua1gmR0ZSNBGwrb/bPhsb/G1w9cr4FtOLx3gCfKnfIUOTDVw01nEQBAoGAdPKDRB2W5+MsBR7iFSXb/jtuXWtYBSDfOEtPnGFtn31g6/PNv3/8gScXEgpCbGxLlD3+lUvAM5RR7qLgaoh1IvtzPHzeXvPi9lLagjuo9ELtUiy5OjVBbCUpN6w2Xbf1kVkW9UTqMF0Mw60VFRzDOcp7F+i7aTXjGDYJRbeDnTA=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk93hq5IOzuhn6qMUWbcIvB5MgpKHEG1bfMWOdLmVj3JzYgEfEPBUYpn7+aktVu3wHblCYZLe40VrxieneZS5/gtHoKVrCymn9dz+OH0crmAQ05mCCNOqwEXEG+NkM30B++UYrPC4lk2hzND0A7WSXCQrXrlaLknz0c9ObSyz64CONY8bS80oX/fwj4k0bVKQoVI1nNZRW9EqT0bsHnOSfjt+X7WvGLUKE+l0TcO0J5g1b5uCU96Cz/TlftFOd1t3Vw9xhTNn+ewhjb+yBPSSnmdeNza2jXDGBqMCgzj2sJQeGp/t4G5RYFnu4FPgb42OLFTtg3V7gj15fb0QkgmqgQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8900/pay/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8900/pay/returnUrl";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "D:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
