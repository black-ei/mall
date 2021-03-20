package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan(value = "com.baidu.shop.mapper")
public class RunOrderServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunOrderServerApplication.class);
    }
}
