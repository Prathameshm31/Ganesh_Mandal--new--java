package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.NotificationConfigDTO;
import com.ganesh.mandal.entity.NotificationConfig;
import com.ganesh.mandal.repository.NotificationConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationConfigService {

    private final NotificationConfigRepository repository;

    @Transactional
    public NotificationConfigDTO getConfig() {
        List<NotificationConfig> all = repository.findAll();
        if (all.isEmpty()) {
            return toDTO(repository.save(NotificationConfig.builder().build()));
        }
        return toDTO(all.get(0));
    }

    @Transactional
    public NotificationConfigDTO update(NotificationConfigDTO dto) {
        List<NotificationConfig> all = repository.findAll();
        NotificationConfig config;
        if (all.isEmpty()) {
            config = new NotificationConfig();
        } else {
            config = all.get(0);
        }
        if (dto.getWhatsappEnabled() != null) config.setWhatsappEnabled(dto.getWhatsappEnabled());
        if (dto.getEmailEnabled() != null) config.setEmailEnabled(dto.getEmailEnabled());
        if (dto.getWhatsappProvider() != null) config.setWhatsappProvider(dto.getWhatsappProvider());
        if (dto.getEmailProvider() != null) config.setEmailProvider(dto.getEmailProvider());
        if (dto.getSmtpHost() != null) config.setSmtpHost(dto.getSmtpHost());
        if (dto.getSmtpPort() != null) config.setSmtpPort(dto.getSmtpPort());
        if (dto.getSmtpUsername() != null) config.setSmtpUsername(dto.getSmtpUsername());
        if (dto.getSmtpPassword() != null) config.setSmtpPassword(dto.getSmtpPassword());
        if (dto.getSenderName() != null) config.setSenderName(dto.getSenderName());
        if (dto.getSenderEmail() != null) config.setSenderEmail(dto.getSenderEmail());
        if (dto.getSenderWhatsapp() != null) config.setSenderWhatsapp(dto.getSenderWhatsapp());
        if (dto.getWhatsappApiKey() != null) config.setWhatsappApiKey(dto.getWhatsappApiKey());
        if (dto.getWhatsappApiUrl() != null) config.setWhatsappApiUrl(dto.getWhatsappApiUrl());
        return toDTO(repository.save(config));
    }

    private NotificationConfigDTO toDTO(NotificationConfig c) {
        return NotificationConfigDTO.builder()
                .id(c.getId()).whatsappEnabled(c.getWhatsappEnabled())
                .emailEnabled(c.getEmailEnabled()).whatsappProvider(c.getWhatsappProvider())
                .emailProvider(c.getEmailProvider()).smtpHost(c.getSmtpHost())
                .smtpPort(c.getSmtpPort()).smtpUsername(c.getSmtpUsername())
                .smtpPassword(c.getSmtpPassword()).senderName(c.getSenderName())
                .senderEmail(c.getSenderEmail()).senderWhatsapp(c.getSenderWhatsapp())
                .whatsappApiKey(c.getWhatsappApiKey()).whatsappApiUrl(c.getWhatsappApiUrl())
                .createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt())
                .build();
    }
}
