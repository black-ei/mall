package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.baidu.shop.mapper")
public class RunAddrService {
    public static void main(String[] args) {
        SpringApplication.run(RunAddrService.class);
    }
}
