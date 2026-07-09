package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    List<NotificationTemplate> findByNotificationType(String notificationType);
    List<NotificationTemplate> findByStatus(String status);
}
