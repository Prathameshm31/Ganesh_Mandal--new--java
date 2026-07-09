package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    List<NotificationHistory> findByStatus(String status);
    List<NotificationHistory> findByChannel(String channel);
    List<NotificationHistory> findByUserId(Long userId);
    List<NotificationHistory> findByEventId(Long eventId);
    List<NotificationHistory> findByNotificationType(String notificationType);
    List<NotificationHistory> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    long countByStatus(String status);
    long countByChannel(String channel);
    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
