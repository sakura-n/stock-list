package com.example.stocklist.service;

import com.example.stocklist.dto.ItemDTO;
import com.example.stocklist.dto.PurchaseListResponse;
import com.example.stocklist.entity.GlobalSettings;
import com.example.stocklist.entity.Item;
import com.example.stocklist.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final SettingsService settingsService;

    public List<ItemDTO> list(String keyword, String category) {
        String kw = StringUtils.hasText(keyword) ? keyword : null;
        String cat = StringUtils.hasText(category) ? category : null;
        GlobalSettings settings = settingsService.get();
        return itemRepository.search(kw, cat).stream()
                .map(item -> toItemDTO(item, settings))
                .toList();
    }

    public ItemDTO getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("物品不存在: " + id));
        return toItemDTO(item, settingsService.get());
    }

    public Item create(Item item) {
        return itemRepository.save(item);
    }

    public Item update(Long id, Item data) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("物品不存在: " + id));
        item.setName(data.getName());
        item.setCategory(data.getCategory());
        item.setStock(data.getStock());
        item.setUnit(data.getUnit());
        item.setExpiryDate(data.getExpiryDate());
        item.setLocation(data.getLocation());
        item.setThreshold(data.getThreshold());
        item.setRemark(data.getRemark());
        item.setPhotoUrl(data.getPhotoUrl());
        return itemRepository.save(item);
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    public List<String> getCategories() {
        return itemRepository.findDistinctCategories();
    }

    public PurchaseListResponse getPurchaseList() {
        GlobalSettings settings = settingsService.get();
        List<Item> all = itemRepository.findAll();

        List<ItemDTO> lowStock = all.stream()
                .filter(item -> isLowStock(item, settings))
                .map(item -> toItemDTO(item, settings))
                .toList();

        List<ItemDTO> expiringSoon = all.stream()
                .filter(item -> isExpiringSoon(item, settings))
                .map(item -> toItemDTO(item, settings))
                .toList();

        return new PurchaseListResponse(lowStock, expiringSoon);
    }

    private boolean isLowStock(Item item, GlobalSettings settings) {
        int effectiveThreshold = item.getThreshold() != null
                ? item.getThreshold()
                : settings.getDefaultThreshold();
        return item.getStock() <= effectiveThreshold;
    }

    private boolean isExpiringSoon(Item item, GlobalSettings settings) {
        if (item.getExpiryDate() == null) {
            return false;
        }
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), item.getExpiryDate());
        return daysLeft >= 0 && daysLeft <= settings.getExpireWarnDays();
    }

    private ItemDTO toItemDTO(Item item, GlobalSettings settings) {
        return ItemDTO.from(item, isLowStock(item, settings), isExpiringSoon(item, settings));
    }
}
