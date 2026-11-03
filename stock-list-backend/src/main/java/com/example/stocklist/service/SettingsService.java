package com.example.stocklist.service;

import com.example.stocklist.entity.GlobalSettings;
import com.example.stocklist.repository.GlobalSettingsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final GlobalSettingsRepository repository;

    @PostConstruct
    public void init() {
        if (repository.findById(1L).isEmpty()) {
            repository.save(new GlobalSettings());
        }
    }

    public GlobalSettings get() {
        return repository.findById(1L).orElseGet(GlobalSettings::new);
    }

    public GlobalSettings update(GlobalSettings settings) {
        GlobalSettings existing = get();
        if (settings.getDefaultThreshold() != null) {
            existing.setDefaultThreshold(settings.getDefaultThreshold());
        }
        if (settings.getExpireWarnDays() != null) {
            existing.setExpireWarnDays(settings.getExpireWarnDays());
        }
        return repository.save(existing);
    }
}
