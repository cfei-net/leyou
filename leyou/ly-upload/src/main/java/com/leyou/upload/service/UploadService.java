package com.leyou.upload.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UploadService {

    // 图片上传的路径
    private static final String IMAGE_PATH = "D:\\leyou-code\\326127\\software\\nginx-1.16.0\\html\\brand-img\\";
    // 图片的访问路径
    private static final String IMAGE_URL = "http://localhost/brand-img/";

    public String upload(MultipartFile file) {
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
}
