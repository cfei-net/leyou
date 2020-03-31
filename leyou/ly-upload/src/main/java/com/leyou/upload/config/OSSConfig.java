package com.leyou.upload.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 配置类
public class OSSConfig {

    /* 如果一个类只是在一个方法中有效，可以直接放到方法的参数中
    @Autowired
    private OSSProperties prop;*/

    @Bean
    public OSS client(OSSProperties prop){
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = prop.getEndpoint();
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。
        // 强烈建议您创建并使用RAM账号进行API访问或日常运维，
        // 请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = prop.getAccessKeyId();
        String accessKeySecret = prop.getAccessKeySecret();
        // 创建OSSClient实例并且返回到spring的容器中
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

}
