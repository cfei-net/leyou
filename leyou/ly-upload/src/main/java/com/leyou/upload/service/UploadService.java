package com.leyou.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.OSSProperties;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class UploadService {

    // 阿里云上传客户端
    @Autowired
    private OSS client;

    // 配置类
    @Autowired
    private OSSProperties prop;


    // 图片上传的路径
    private static final String IMAGE_PATH = "D:\\leyou-code\\326127\\software\\nginx-1.16.0\\html\\brand-img\\";
    // 图片的访问路径
    private static final String IMAGE_URL = "http://image.leyou.com/brand-img/";

    // 允许上传的图片类型
    private static final List<String> ALLOW_UPLOAD_CONTENT_TYPE = Arrays.asList("image/png","image/jpg","image/jpeg");

    public String upload(MultipartFile file) {
        //  获取文件的类型： content-type
        String contentType = file.getContentType();
        // 判断是否在允许的类型范围内
        if(!ALLOW_UPLOAD_CONTENT_TYPE.contains(contentType)){
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        // 上面我们已经控制了文件的类型，但是有可能一些非法分子会改后缀名，所以我们需要进一步的控制
        try {
            // 如果是图片，返回值不会为空；如果为空，则不是图片。
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage==null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        // 文件夹
        File imagePath = new File(IMAGE_PATH);
        // 图片的名称: 图片的名字，加入uuid作为名称
        String imageName = UUID.randomUUID()+file.getOriginalFilename();
        try {
            // 图片的上传
            file.transferTo(new File(imagePath, imageName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
        // 拼接图片的访问路径
        String imgUrl = IMAGE_URL + imageName;
        // 返回图片的路径
        return imgUrl;
    }

    /**
     * 生成签名等信息
     * @return 签名等信息
     */
    public Map<String, String> signature() {
        try {
            long expireTime = prop.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;// 转成毫秒数
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, prop.getMaxFileSize()); // 上传大小
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, prop.getDir()); // 目录

            String postPolicy = client.generatePostPolicy(expiration, policyConds); // 生成上传的策略
            byte[] binaryData = postPolicy.getBytes("utf-8"); // 编码格式
            String encodedPolicy = BinaryUtil.toBase64String(binaryData); // 把数据转成base64编码
            String postSignature = client.calculatePostSignature(postPolicy); // 生成签名

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessId", prop.getAccessKeyId()); // 要与我们的文档一致
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", prop.getDir());
            respMap.put("host", prop.getHost()); // 文件上传到哪里
            respMap.put("expire", String.valueOf(expireEndTime)); // 毫秒数
            log.info("【上传微服务】获取上传到阿里云的签名成功");
            return respMap;
        } catch (Exception e) {
            log.error("【上传微服务】获取签名失败：{}", e.getMessage());
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
    }
}
