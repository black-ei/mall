package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(value = "com.baidu.shop.mapper")
@EnableEurekaClient
public class RunUserServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunUserServerApplication.class);
    }
}
