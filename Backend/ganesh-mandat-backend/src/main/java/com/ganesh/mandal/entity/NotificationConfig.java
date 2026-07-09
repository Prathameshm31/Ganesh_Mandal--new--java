package com.ganesh.mandal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "whatsapp_enabled")
    @Builder.Default
    private Boolean whatsappEnabled = false;

    @Column(name = "email_enabled")
    @Builder.Default
    private Boolean emailEnabled = false;

    @Column(name = "whatsapp_provider", length = 100)
    private String whatsappProvider;

    @Column(name = "email_provider", length = 100)
    private String emailProvider;

    @Column(name = "smtp_host", length = 200)
    private String smtpHost;

    @Column(name = "smtp_port")
    private Integer smtpPort;

    @Column(name = "smtp_username", length = 200)
    private String smtpUsername;

    @Column(name = "smtp_password", length = 200)
    private String smtpPassword;

    @Column(name = "sender_name", length = 200)
    private String senderName;

    @Column(name = "sender_email", length = 200)
    private String senderEmail;

    @Column(name = "sender_whatsapp", length = 50)
    private String senderWhatsapp;

    @Column(name = "whatsapp_api_key", length = 500)
    private String whatsappApiKey;

    @Column(name = "whatsapp_api_url", length = 500)
    private String whatsappApiUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
