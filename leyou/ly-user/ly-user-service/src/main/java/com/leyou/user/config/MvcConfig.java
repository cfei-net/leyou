package com.leyou.user.config;

import com.leyou.user.interceptor.PrivilegeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private PrivilegeInterceptor privilegeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 放行地址访问：或者把服务鉴权配置好，订单微服务参考用户微服务去做就可以了
        registry.addInterceptor(privilegeInterceptor).excludePathPatterns("/address/**","/swagger-ui.html");
    }
}