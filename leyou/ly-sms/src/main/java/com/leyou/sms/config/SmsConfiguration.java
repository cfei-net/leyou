package com.leyou.sms.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfiguration {
    /**
     * 第一种方式注入
     */
    /*@Autowired
    private SmsProperties prop;*/

    /**
     *  发送短信的客户端类
     */
    @Bean
    public IAcsClient acsClient(SmsProperties prop){ // 第二种注入：直接写在方法参数中
        DefaultProfile profile = DefaultProfile.getProfile(
                prop.getRegionID(),
                prop.getAccessKeyID(),
                prop.getAccessKeySecret());
        return new DefaultAcsClient(profile);
    }
}
