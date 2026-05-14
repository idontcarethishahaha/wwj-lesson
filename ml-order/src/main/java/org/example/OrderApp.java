package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("org.example.mapper")// 扫描mapper接口
@EnableDiscoveryClient// 启动服务注册于发现
public class OrderApp {
    public static void main(String[] args) {
       SpringApplication.run(OrderApp.class,args);
    }
}