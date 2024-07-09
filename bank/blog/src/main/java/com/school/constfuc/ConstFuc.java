package com.school.constfuc;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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
    static final String UPLOAD_DIR = "blog/src/main/resources/static/image/blog/"; // 上传目录
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
            String fileName =  processFileName(Objects.requireNonNull(file.getOriginalFilename()));
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

    public static String processFileName(String originalFileName) {
        // 生成随机 UUID 作为前缀
        String uuid = UUID.randomUUID().toString();
        if(originalFileName == null){
            return uuid;
        }
        // 获取文件名和扩展名
        int dotIndex = originalFileName.lastIndexOf('.');
        String fileNameWithoutExtension = (dotIndex == -1) ? originalFileName : originalFileName.substring(0, dotIndex);
        String extension = (dotIndex == -1) ? "" : originalFileName.substring(dotIndex);

        // 提取前 6 个字符并替换汉字
        StringBuilder processedFileNameBuilder = new StringBuilder();
        int count = 0;
        for (int i = 0; i < fileNameWithoutExtension.length() && count < 6; i++) {
            char ch = fileNameWithoutExtension.charAt(i);
            if (isChineseCharacter(ch)) {
                processedFileNameBuilder.append('a');
            } else {
                processedFileNameBuilder.append(ch);
                count++;
            }
        }

        // 构建处理后的文件名
        return uuid + "_" + processedFileNameBuilder.toString() + extension;
    }

    // 检查字符是否为汉字
    private static boolean isChineseCharacter(char ch) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

}
