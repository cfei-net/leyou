package com.leyou.gateway.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 公钥： 除了授权中心之外，其他的微服务只能持有公钥
 */
//@Data
//@Slf4j
@ConfigurationProperties("ly.jwt")
public class JwtProperties implements InitializingBean {
    private String pubKeyPath;
    private PublicKey publicKey; // 公钥
    /**跟用户登录token相关的属性*/
    private UserTokenProperties user = new UserTokenProperties();//一定要实例化

    //微服务申请token的id和密码
    private AppTokenProperties app = new AppTokenProperties();

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public UserTokenProperties getUser() {
        return user;
    }

    public void setUser(UserTokenProperties user) {
        this.user = user;
    }

    public AppTokenProperties getApp() {
        return app;
    }

    public void setApp(AppTokenProperties app) {
        this.app = app;
    }

    @Data
    public class AppTokenProperties {
        private Long id;
        private String secret;
        private String headerName;

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
    @Data
    public class UserTokenProperties {
        /**存放token的cookie名称*/
        private String cookieName;

        public String getCookieName() {
            return cookieName;
        }

        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
            //log.info("【乐优网关】加载公钥成功");
            System.out.println("【乐优网关】加载公钥成功");
        } catch (Exception e) {
            //log.error("【乐优网关】加载公钥失败,原因：{}", e.getMessage());
            System.out.println("【乐优网关】加载公钥失败,原因："+e.getMessage());
            throw new RuntimeException("公钥加载失败",e);
        }
    }


}
