package com.leyou.auth.config;

import com.leyou.common.auth.utils.RsaUtils;
import com.leyou.common.exception.LyException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 公钥和私钥： 不能每次调用的时候生成，如果每次调用生成，性能很差
 *              我们应该让授权中心启动的时候，就马上加载公钥和私钥，放入内存中
 */
//@Data
//@Slf4j
@ConfigurationProperties("ly.jwt")
public class JwtProperties implements InitializingBean {
    private String pubKeyPath;
    private String priKeyPath;

    private PublicKey publicKey; // 公钥
    private PrivateKey privateKey; // 私钥
    /**
     * 跟用户登录token相关的属性
     */
    private UserTokenProperties user = new UserTokenProperties();//一定要实例化

    private AppTokenProperties app = new AppTokenProperties();

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public String getPriKeyPath() {
        return priKeyPath;
    }

    public void setPriKeyPath(String priKeyPath) {
        this.priKeyPath = priKeyPath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
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
    public class AppTokenProperties{
        private int expire;
        private Long id;
        private String name;
        private String headerName;

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Data
    public class UserTokenProperties {
        /**
         * 过期时间，单位分钟
         */
        private int expire;
        /**
         * 存放token的cookie名称
         */
        private String cookieName;
        /**
         * 存放token的cookie的domain
         */
        private String cookieDomain;
        /**
         * 刷新token的时间，单位分钟，如果小于这个时间，则重新生成token
         */
        private int refreshTime;

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }

        public String getCookieName() {
            return cookieName;
        }

        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        public String getCookieDomain() {
            return cookieDomain;
        }

        public void setCookieDomain(String cookieDomain) {
            this.cookieDomain = cookieDomain;
        }

        public int getRefreshTime() {
            return refreshTime;
        }

        public void setRefreshTime(int refreshTime) {
            this.refreshTime = refreshTime;
        }
    }
    /**
     * 我们必须要等到当前对象实例化之后，才能获取公钥和私钥的路径地址
     * 我们可以通过Spring的生命周期对象，来创建对象
     *
     * afterPropertiesSet: 这个方法是spring容器的对象全部创建完成之后调用的
     *
     * 利用这个特点，加载公钥和私钥
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
            privateKey = RsaUtils.getPrivateKey(priKeyPath);
            //log.info("【授权中心】加载公钥和私钥成功");
            System.out.println("【授权中心】加载公钥和私钥成功");
        } catch (Exception e) {
            //log.error("【授权中心】加载公钥和私钥失败,原因：{}", e.getMessage());
            System.err.println("【授权中心】加载公钥和私钥失败,原因："+e.getMessage());
            throw new RuntimeException("公钥和私钥加载失败",e);
        }
    }


}
