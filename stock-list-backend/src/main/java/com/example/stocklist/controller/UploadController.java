package com.example.stocklist.controller;

import com.example.stocklist.dto.ApiResponse;
import com.example.stocklist.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "03-图片上传", description = "物品照片上传，返回可直接访问的相对路径，回填到物品的 photoUrl 字段")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UploadController {

    private final FileUploadService fileUploadService;

    @Operation(summary = "上传物品照片",
            description = "以 multipart/form-data 方式上传图片，字段名为 file。"
                    + "仅支持 jpg/png/webp，大小不超过 5MB；服务端按日期目录 + UUID 文件名保存到 uploads/ 下，"
                    + "返回相对路径（如 /uploads/20261030/ab12cd34.jpg），"
                    + "通过静态资源映射直接 GET 访问；保存物品时将该路径写入 photoUrl 字段")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> upload(
            @Parameter(description = "图片文件，form-data 字段名固定为 file", required = true)
            @RequestPart("file") MultipartFile file) {
        return ApiResponse.ok(fileUploadService.store(file));
    }
}
