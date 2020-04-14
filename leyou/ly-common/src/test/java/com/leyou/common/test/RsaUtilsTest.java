package com.leyou.common.test;

import com.leyou.common.auth.utils.RsaUtils;
import org.junit.Test;

public class RsaUtilsTest {

    private final static String publicKeyFilename = "D:\\leyou-code\\326127\\rsa\\id_rsa.pub";
    private final static String privateKeyFilename = "D:\\leyou-code\\326127\\rsa\\id_rsa";   // keySize： 指的是私钥的长度

    /**
     * 测试生成公钥和私钥
     * @throws Exception
     */
    @Test
    public void generateKey() throws Exception {
        RsaUtils.generateKey(publicKeyFilename,privateKeyFilename,"xiaofeifei@123!",0);
    }

    /**
     * 测试获取公钥和私钥
     * @throws Exception
     */
    @Test
    public void getKey() throws Exception {
        System.out.println("公钥："+RsaUtils.getPublicKey(publicKeyFilename));
        System.out.println("私钥："+RsaUtils.getPrivateKey(privateKeyFilename));
    }
}