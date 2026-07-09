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
public class NotificationConfigDTO {
    private Long id;
    private Boolean whatsappEnabled;
    private Boolean emailEnabled;
    private String whatsappProvider;
    private String emailProvider;
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private String senderName;
    private String senderEmail;
    private String senderWhatsapp;
    private String whatsappApiKey;
    private String whatsappApiUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
