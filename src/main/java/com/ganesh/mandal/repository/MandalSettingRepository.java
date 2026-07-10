package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.MandalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MandalSettingRepository extends JpaRepository<MandalSetting, Long> {
    Optional<MandalSetting> findBySettingKey(String settingKey);
}
