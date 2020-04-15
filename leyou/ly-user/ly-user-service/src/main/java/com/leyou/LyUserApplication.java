package com.leyou;

import com.leyou.user.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.user.mapper")
@EnableConfigurationProperties(JwtProperties.class)
public class LyUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyUserApplication.class, args);
    }
}
