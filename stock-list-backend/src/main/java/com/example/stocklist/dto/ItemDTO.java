package com.example.stocklist.dto;

import com.example.stocklist.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(name = "ItemDTO", description = "库存物品视图：物品字段 + 实时计算的提醒标记")
public class ItemDTO {

    @Schema(description = "主键 ID", example = "2")
    private Long id;
    @Schema(description = "物品名称", example = "大米")
    private String name;
    @Schema(description = "品类", example = "食品")
    private String category;
    @Schema(description = "当前库存数量", example = "2")
    private Integer stock;
    @Schema(description = "库存单位", example = "袋")
    private String unit;
    @Schema(description = "有效期 yyyy-MM-dd，可为空", example = "2031-01-01")
    private LocalDate expiryDate;
    @Schema(description = "存放地点", example = "阳台柜子")
    private String location;
    @Schema(description = "单品库存阈值，null 表示使用全局默认阈值", example = "3", nullable = true)
    private Integer threshold;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "物品照片访问路径，null 表示未上传；前端需拼接服务端域名后访问", example = "/uploads/20261030/ab12cd34.jpg", nullable = true)
    private String photoUrl;
    @Schema(description = "库存不足标记：stock ≤ 生效阈值（单品阈值，未设置则全局默认）时为 true")
    private boolean lowStock;
    @Schema(description = "临期标记：有效期距今 0~预警天数时为 true，已过期不计入")
    private boolean expiringSoon;

    public static ItemDTO from(Item item, boolean lowStock, boolean expiringSoon) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setCategory(item.getCategory());
        dto.setStock(item.getStock());
        dto.setUnit(item.getUnit());
        dto.setExpiryDate(item.getExpiryDate());
        dto.setLocation(item.getLocation());
        dto.setThreshold(item.getThreshold());
        dto.setRemark(item.getRemark());
        dto.setPhotoUrl(item.getPhotoUrl());
        dto.setLowStock(lowStock);
        dto.setExpiringSoon(expiringSoon);
        return dto;
    }
}
