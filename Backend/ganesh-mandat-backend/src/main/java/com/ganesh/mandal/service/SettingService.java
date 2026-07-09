package com.ganesh.mandal.service;

import com.ganesh.mandal.entity.MandalSetting;
import com.ganesh.mandal.repository.MandalSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final MandalSettingRepository repository;

    @Transactional(readOnly = true)
    public Map<String, String> getAllSettings() {
        List<MandalSetting> settings = repository.findAll();
        Map<String, String> map = new HashMap<>();
        for (MandalSetting s : settings) {
            map.put(s.getSettingKey(), s.getSettingValue());
        }
        return map;
    }

    @Transactional(readOnly = true)
    public String getSetting(String key) {
        return repository.findBySettingKey(key)
                .map(MandalSetting::getSettingValue)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCollectionGoal() {
        String val = getSetting("collection_goal");
        if (val == null) return BigDecimal.valueOf(1000000);
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return BigDecimal.valueOf(1000000);
        }
    }

    @Transactional
    public void updateSetting(String key, String value) {
        MandalSetting setting = repository.findBySettingKey(key)
                .orElse(MandalSetting.builder().settingKey(key).build());
        setting.setSettingValue(value);
        repository.save(setting);
    }

    @Transactional
    public void updateSettings(Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            updateSetting(entry.getKey(), entry.getValue());
        }
    }
}
