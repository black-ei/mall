package com.baidu.global;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new
                UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 允许cookies跨域
        config.addAllowedOrigin("*");// 允许向该服务器提交请求的URI，*表示全部允许。。
        // 这里尽量限制来源域，比如http:xxxx:8080 ,以降低安全风险。。
        config.addAllowedHeader("*");// 允许访问的头信息,*表示全部
        config.setMaxAge(18000L);// 预检请求的缓存时间（秒），即在这个时间段里，对于相同
        config.addAllowedMethod("*");// 允许提交请求的方法，*表示全部允许，也可以单独设
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");// 允许Get的请求方法
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);//3.返回新的CorsFilter.
        return new CorsFilter(source);
    }

  /*  private static int a =0;
    private static Integer b =0;

    public void test() throws InterruptedException {
        for(int i=0;i<20;i++){
            new Thread(()->{
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                a++;
                b++;
            }).start();
        }
        Thread.sleep(1);
        System.out.println(a);
        System.out.println(b);
    }
*/
/*    public static void main(String[] args) throws InterruptedException {

        for(int i=0;i<2000;i++) {
            new Thread(() -> {
                T t = new T();
                t.add();
            }).start();
        }
        System.out.println(T.a);
    }*/

}
