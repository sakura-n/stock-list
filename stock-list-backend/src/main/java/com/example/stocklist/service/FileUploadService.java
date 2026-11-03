package com.example.stocklist.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

/**
 * 物品照片上传：保存到工作目录下的 uploads/yyyyMMdd/ 中，文件名使用 UUID 防止冲突与路径注入，
 * 返回可供静态资源映射访问的相对路径 /uploads/...
 */
@Slf4j
@Service
public class FileUploadService {

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024L;

    private static final Map<String, String> EXT_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private static final DateTimeFormatter DIR_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    /** 上传根目录，与 WebConfig 的静态资源映射保持一致（默认用户主目录） */
    @Value("${app.upload-dir}")
    private String uploadDir;

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的图片文件");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("图片大小不能超过 5MB");
        }
        String contentType = file.getContentType();
        String ext = contentType == null ? null : EXT_BY_CONTENT_TYPE.get(contentType);
        if (ext == null) {
            throw new IllegalArgumentException("仅支持 jpg / png / webp 格式的图片");
        }

        String dateDir = LocalDate.now().format(DIR_FORMAT);
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        try {
            Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path targetDir = baseDir.resolve(dateDir);
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(fileName);
            file.transferTo(target);
            log.info("照片已保存: {}", target);
            return "/uploads/" + dateDir + "/" + fileName;
        } catch (IOException e) {
            log.error("照片保存失败", e);
            throw new IllegalStateException("照片保存失败，请重试");
        }
    }
}
