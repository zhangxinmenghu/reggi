package com.example.controller;

import com.example.common.R;
import com.sun.deploy.net.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


//文件上传和下载
@RequestMapping("/common")
@RestController
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        //原始文件名
        String originalFilename = file.getOriginalFilename();//abc.jpg
        //后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String filename = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        file.transferTo(new File(basePath + filename));

        return R.success(filename);
    }

    //文件下载
    @GetMapping("/download")
    public void downlaod(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片了
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("/image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ( (len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}