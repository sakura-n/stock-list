package com.example.stocklist.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "global_settings")
@Schema(name = "GlobalSettings", description = "购买提醒的全局单行配置（首次启动自动初始化）")
public class GlobalSettings {

    @Schema(description = "固定为 1，全局配置仅单行", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    private Long id = 1L;

    @Schema(description = "默认库存阈值：未单独设置阈值的物品，库存 ≤ 此值即判定为库存不足；初始值 1", example = "1")
    @Column(nullable = false)
    private Integer defaultThreshold = 1;

    @Schema(description = "过期预警天数：有效期距今 0~此天数即判定为临期；初始值 7", example = "7")
    @Column(nullable = false)
    private Integer expireWarnDays = 7;
}
