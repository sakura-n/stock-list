package com.example.stocklist.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "item")
@Schema(name = "Item", description = "库存物品：囤货清单的一条记录，含品名、库存、效期、存放地点与购买阈值")
public class Item {

    @Schema(description = "主键 ID，新建时由数据库自增生成，请求体中无需传", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "物品名称（必填）", requiredMode = Schema.RequiredMode.REQUIRED, example = "大米")
    @NotBlank(message = "名称不能为空")
    @Column(nullable = false)
    private String name;

    @Schema(description = "品类，自由文本，已录入品类会出现在 /api/categories 下拉中", example = "食品")
    @Column
    private String category;

    @Schema(description = "当前库存数量，缺省 0；与生效阈值比较判定是否需购买", example = "2")
    @Column(nullable = false)
    private Integer stock = 0;

    @Schema(description = "库存单位，缺省「件」", example = "袋")
    @Column
    private String unit = "件";

    @Schema(description = "有效期，格式 yyyy-MM-dd，可为空", example = "2027-06-30")
    @Column
    private LocalDate expiryDate;

    @Schema(description = "存放地点，自由文本", example = "阳台柜子")
    @Column
    private String location;

    @Schema(description = "该物品的库存下限阈值：库存 ≤ 此值时进入待购清单；null 表示使用全局默认阈值", example = "3", nullable = true)
    @Column
    private Integer threshold;

    @Schema(description = "备注，最长 500 字", example = "一次买两袋轮换着吃")
    @Column(length = 500)
    private String remark;

    @Schema(description = "物品照片的访问路径，由 /api/upload 接口返回；null 表示未上传照片", example = "/uploads/20261030/ab12cd34.jpg", nullable = true)
    @Column(length = 300)
    private String photoUrl;

    @Schema(description = "创建时间，服务端自动生成，请求中忽略", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间，服务端自动维护", accessMode = Schema.AccessMode.READ_ONLY)
    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
