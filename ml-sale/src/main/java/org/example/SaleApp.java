package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

// 启用定时任务
@EnableScheduling
@MapperScan("org.example.mapper")
@SpringBootApplication
@EnableDiscoveryClient
// 启用Feign
//@EnableFeignClients
public class SaleApp {
    public static void main(String[] args) {
        SpringApplication.run(SaleApp.class, args);
    }
}
