package com.example.stocklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "统一响应包装：所有接口均返回此结构，业务数据在 data 字段中；泛参实例化后的子类型由 springdoc 自动生成，勿固定 name")
public class ApiResponse<T> {

    @Schema(description = "业务状态码：0 成功，-1 失败", example = "0")
    private int code;

    @Schema(description = "提示信息：成功为 success，失败时为错误描述", example = "success")
    private String message;

    @Schema(description = "业务数据，失败时为 null", nullable = true)
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "success", data);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(0, "success", null);
    }

    public static <T> ApiResponse<T> fail(String msg) {
        return new ApiResponse<>(-1, msg, null);
    }
}
