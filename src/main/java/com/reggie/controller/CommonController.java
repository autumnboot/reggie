package com.reggie.controller;

import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

//图片上传和下载
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    //解耦文件转存路径
    @Value("${reggie.path}")
    private String basePath;
    //图片上传
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //此时file是临时文件，需要转存到指定位置，否则本次请求完成后会删除
        log.info(file.toString());
        String originalFilename = file.getOriginalFilename();
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID生成文件名，防止因重名而导致的文件覆盖
        String fileName = UUID.randomUUID().toString() + suffixName;

        //创建目录
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    //下载图片到浏览器
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //通过输入流读取图片内容
            FileInputStream inputStream = new FileInputStream(new File(basePath + name));
            //图片响应类型
            response.setContentType("image/jpeg");
            //通过输出流写到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = inputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
