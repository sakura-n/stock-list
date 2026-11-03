package com.example.stocklist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 上传目录：默认落在用户主目录（见 application.yml 的 app.upload-dir），
     * 打包为桌面程序后程序所在目录只读，必须写到用户可写位置
     */
    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
        registry.addMapping("/uploads/**")
                .allowedOrigins("*")
                .allowedMethods("GET");
    }

    /**
     * 将上传目录映射为静态资源，照片通过 /uploads/** 直接访问。
     * 注意：必须先确保目录存在，否则 Path.toUri() 不会追加尾部斜杠，资源位置解析会失败导致 404。
     * 前端页面由 Spring Boot 默认的 static/index.html 提供（根路径 / 直接返回首页）。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadsDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadsDir);
        } catch (IOException e) {
            throw new UncheckedIOException("创建上传目录失败: " + uploadsDir, e);
        }
        String location = uploadsDir.toUri().toString();
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
