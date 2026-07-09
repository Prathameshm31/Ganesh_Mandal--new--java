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
public class NotificationTemplateDTO {
    private Long id;
    private String templateName;
    private String notificationType;
    private String whatsappTemplateId;
    private String emailSubject;
    private String emailBody;
    private String messageText;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
