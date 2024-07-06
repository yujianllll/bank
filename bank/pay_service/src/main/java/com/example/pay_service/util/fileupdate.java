package com.example.pay_service.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class fileupdate {
    static final String UPLOAD_DIR = "pay_service/src/main/resources/static/image/comment/"; // 上传目录
    public String saveFile(MultipartFile file, String dir) { // 保存文件
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
            if (dir == null) { dir = ""; }
            String uploadDir = UPLOAD_DIR + dir;
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
}
