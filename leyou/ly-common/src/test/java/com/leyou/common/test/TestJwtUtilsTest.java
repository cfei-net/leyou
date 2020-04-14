package com.leyou.common.test;


import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.utils.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class TestJwtUtilsTest {

    private final static String publicKeyFilename = "D:\\leyou-code\\326127\\rsa\\id_rsa.pub";
    private final static String privateKeyFilename = "D:\\leyou-code\\326127\\rsa\\id_rsa";   // keySize： 指的是私钥的长度

    /**
     * 生成一个5分钟后过期的token
     */
    @Test
    public void testGenToken() throws Exception {
        // 当前登录用户的信息
        UserInfo u = new UserInfo(31L, "xiaofeifei", "superadmin");
        // 获取私钥
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyFilename);
        // 生成token
        String token = JwtUtils.generateTokenExpireInMinutes(u, privateKey, 5);
        // 打印
        System.out.println("五分钟后失效的token："+token);
    }


    /**
     * 解析token
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        // 用户操作的时候，会把这个token带过来
        String token = "eyJhbGciOiJSUzI1NiJ9." +
                "eyJ1c2VyIjoie1wiaWRcIjozMSxcInVzZXJuYW1lXCI6XCJ4aWFvZmVpZmVpXCIsXCJyb2xlXCI6XCJzdXBlcmFkbWluXCJ9IiwianRpIjoiWkRaaU5EQmtNRFV0Wm1KbE9TMDBORGhpTFRoak5ESXROR1ZqWXpjd1pXRTFNR05qIiwiZXhwIjoxNTg2ODQ3NTcwfQ." +
                "c4IO8WixeWHI1_DMy1-X5kiN1dEF-DRL4VExLhjlEeChhjGVwjN0H4qiXOz9p9lbrsSQjPzFFexmUQipAkmLOC_kQatIetCRQ5frm1UtNCNfnJ26CqVUXWZaQAdKwDypih3TFTsgDH2bqloi50_9MczZ3E-RD10opivCCIxq474vGGQHTvoVIgEsBtlJJmmwoMqaHCBzuYzLT6la8rBTgJaTv0_WLHlNF7LEPrbcERGqthK2_-cb-r1j7OvME6jimcp7wQ4dw6aua2oHGzcqs2u8hE0h1q8dxwpnJfd8FFJB7Jco-4ZHcxnwGE3G-VZFknQ-DRwEWaR9AdJkpxJtWA";
        // 获取公钥
        PublicKey publicKey = RsaUtils.getPublicKey(publicKeyFilename);
        // 后台解析token
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, publicKey, UserInfo.class);
        // 获取用户角色
        UserInfo userInfo = payload.getUserInfo();
        String role = userInfo.getRole();
        String username = userInfo.getUsername();
        Long id = userInfo.getId();
        System.out.println(username);
        System.out.println(role);
        System.out.println(id);
        System.out.println(payload.getExpiration());
    }
}