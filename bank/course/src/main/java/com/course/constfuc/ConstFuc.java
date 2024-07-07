package com.course.constfuc;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

/**
 * @ClassName:ConstFuc
 * @Author:DC
 * @Date:2024/6/30 16:47
 * @version:1.0
 * @Description:函数类
 */
@Component
public class ConstFuc {
    public boolean deleteFile(String filePath) { // 删除文件
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                return true;
            } else {
                System.out.println("文件不存在: " + filePath);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String saveFile(MultipartFile file, String uploadDir) { // 保存文件
        if (file.isEmpty()) {
            return null;
        }
        long fileSize = file.getSize();
        String fileType = file.getContentType();
        // 图片文件大小限制为10MB，超过限制则抛出异常
        if (fileType != null && fileType.startsWith("image/") && fileSize > 10 * 1024 * 1024) {
            return null;
        }
        try {
            // 获取上传目录
            if (uploadDir == null) {
                return null;
            }
            Path uploadPath = Paths.get(uploadDir);

            // 检查目录是否存在，不存在则创建
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            // 保存文件
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());
            // 返回文件路径
            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long getDurationBackMillis(MultipartFile multipartFile){
        if(!multipartFile.isEmpty()){
            try{
                // 根据上传的文件名字，构建初始化的文件对象（临时文件），这个文件是空的
                File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                // 通过工具类，将文件拷贝到空的文件对象中
                FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
                // 将普通文件对象转换为媒体对象
                MultimediaObject multimediaObject = new MultimediaObject(file);
                // 获取媒体对象的信息对象
                MultimediaInfo info = multimediaObject.getInfo();
                // 从媒体信息对象中获取媒体的时长，单位是毫秒，需要除以1000转化为秒s
                Long duration = info.getDuration()/1000;
                System.out.println("时长：" + duration);
                // 删除临时文件
                file.delete();

                return duration;
            } catch(Exception e){
                e.printStackTrace();
                return 0L;
            }
        }
        return 0L;
    }

}