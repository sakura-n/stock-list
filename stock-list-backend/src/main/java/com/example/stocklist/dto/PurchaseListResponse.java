package com.example.stocklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PurchaseListResponse", description = "待购买清单：库存不足与即将过期两组物品")
public class PurchaseListResponse {

    @Schema(description = "库存不足组：库存 ≤ 生效阈值（单品阈值，未设置则全局默认）的物品列表")
    private List<ItemDTO> lowStock;

    @Schema(description = "即将过期组：有效期距今 0~预警天数的物品列表，已过期的不计入")
    private List<ItemDTO> expiringSoon;
}
