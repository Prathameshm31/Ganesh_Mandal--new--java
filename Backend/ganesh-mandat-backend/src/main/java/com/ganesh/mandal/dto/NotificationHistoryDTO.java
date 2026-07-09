package com.ganesh.mandal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationHistoryDTO {
    private Long id;
    private Long userId;
    private Long eventId;
    private String notificationType;
    private String channel;
    private String receiver;
    private String message;
    private String status;
    private String errorMessage;
    private LocalDateTime sentTime;
    private LocalDateTime createdAt;
}
