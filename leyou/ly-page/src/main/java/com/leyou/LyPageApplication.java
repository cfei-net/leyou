package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients // 开启远程调用
@EnableDiscoveryClient
public class LyPageApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyPageApplication.class, args);
    }
}
