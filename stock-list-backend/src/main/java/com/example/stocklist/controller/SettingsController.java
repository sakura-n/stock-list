package com.example.stocklist.controller;

import com.example.stocklist.dto.ApiResponse;
import com.example.stocklist.entity.GlobalSettings;
import com.example.stocklist.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "02-全局设置", description = "购买提醒的全局参数：默认库存阈值、过期预警天数。"
        + "未单独设置阈值的物品会使用这里的默认阈值")
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @Operation(summary = "查询全局设置",
            description = "返回当前生效的全局提醒参数：defaultThreshold（默认库存阈值，库存 ≤ 此值判定为需购买，初始为 1）、"
                    + "expireWarnDays（过期预警天数，有效期距今 ≤ 此天数判定为临期，初始为 7）。系统首次启动时自动初始化单行配置。")
    @GetMapping
    public ApiResponse<GlobalSettings> get() {
        return ApiResponse.ok(settingsService.get());
    }

    @Operation(summary = "修改全局设置",
            description = "部分更新全局参数：仅覆盖请求中非 null 的字段，未传字段保持原值。"
                    + "修改后立即生效，列表页提醒标记与待购清单均按新参数实时重新计算。")
    @PutMapping
    public ApiResponse<GlobalSettings> update(@RequestBody GlobalSettings settings) {
        return ApiResponse.ok(settingsService.update(settings));
    }
}
