package com.example.stocklist.controller;

import com.example.stocklist.dto.ApiResponse;
import com.example.stocklist.dto.ItemDTO;
import com.example.stocklist.dto.PurchaseListResponse;
import com.example.stocklist.entity.Item;
import com.example.stocklist.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "01-库存物品管理", description = "囤货清单核心接口：物品的新增、修改、删除、查询与搜索筛选，"
        + "列表与详情返回中附带 lowStock（库存不足）/ expiringSoon（即将过期）提醒标记")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "查询物品列表",
            description = "按关键字和品类筛选库存物品，两个条件均可省略（省略即查全部）。"
                    + "keyword 对名称、品类做不区分大小写的模糊匹配；category 为精确匹配。"
                    + "结果中每条物品附带实时计算的低库存/临期标记，前端据此渲染红色/黄色标签。",
            parameters = {
                    @Parameter(name = "keyword", description = "搜索关键字，模糊匹配名称或品类；不传或空串表示不过滤"),
                    @Parameter(name = "category", description = "品类精确过滤值，取自 /api/categories 返回的品类列表；不传或空串表示全部品类")
            })
    @GetMapping("/items")
    public ApiResponse<List<ItemDTO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return ApiResponse.ok(itemService.list(keyword, category));
    }

    @Operation(summary = "查询物品详情",
            description = "按主键 ID 查询单个物品的完整信息（含提醒标记），用于编辑页回显。物品不存在时返回错误信息。")
    @GetMapping("/items/{id}")
    public ApiResponse<ItemDTO> getById(
            @Parameter(description = "物品主键 ID", required = true)
            @PathVariable Long id) {
        return ApiResponse.ok(itemService.getById(id));
    }

    @Operation(summary = "新增物品",
            description = "录入一条库存物品。仅 name 必填；stock 缺省为 0，unit 缺省为「件」。"
                    + "threshold 为该物品的库存下限阈值，留空（null）时使用全局默认阈值（见 /api/settings）；"
                    + "expiryDate 格式为 yyyy-MM-dd，可为空。")
    @PostMapping("/items")
    public ApiResponse<Item> create(@Valid @RequestBody Item item) {
        return ApiResponse.ok(itemService.create(item));
    }

    @Operation(summary = "修改物品",
            description = "按 ID 全量更新物品的业务字段（名称、品类、库存、单位、有效期、存放地点、阈值、备注）。"
                    + "创建时间与更新时间由服务端自动维护，请求中的相应字段会被忽略。")
    @PutMapping("/items/{id}")
    public ApiResponse<Item> update(
            @Parameter(description = "待修改物品的主键 ID", required = true)
            @PathVariable Long id, @Valid @RequestBody Item item) {
        return ApiResponse.ok(itemService.update(id, item));
    }

    @Operation(summary = "删除物品",
            description = "按 ID 物理删除一条库存物品，删除后该物品不再出现在列表与待购清单中。")
    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "待删除物品的主键 ID", required = true)
            @PathVariable Long id) {
        itemService.delete(id);
        return ApiResponse.ok();
    }

    @Operation(summary = "查询品类列表",
            description = "返回当前所有已录入物品的品类去重集合（按品类名排序），"
                    + "供前端录入页的品类下拉框与列表页的品类筛选使用。")
    @GetMapping("/categories")
    public ApiResponse<List<String>> categories() {
        return ApiResponse.ok(itemService.getCategories());
    }

    @Operation(summary = "查询待购买清单",
            description = "返回需要提醒购买的两组物品："
                    + "lowStock —— 库存 ≤ 生效阈值的物品，生效阈值 = 物品自身 threshold，未设置则回退全局默认阈值；"
                    + "expiringSoon —— 有效期距今 0~N 天的物品，N 为全局过期预警天数（见 /api/settings），已过期（早于今天）的不计入。"
                    + "两组均按当前设置实时计算。")
    @GetMapping("/purchase-list")
    public ApiResponse<PurchaseListResponse> purchaseList() {
        return ApiResponse.ok(itemService.getPurchaseList());
    }
}
