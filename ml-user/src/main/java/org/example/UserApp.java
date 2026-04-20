package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@MapperScan("org.example.mapper")
@EnableDiscoveryClient
@SpringBootApplication
public class UserApp {
    public static void main(String[] args) {
        SpringApplication.run(UserApp.class, args);
    }
}
// 运行UserApp后，访问接口文档地址
//http://localhost:24102/doc.html#/home

//左侧Services(六边形+三角形) 加号 SpringBoot 应用
