package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.NotificationTemplateDTO;
import com.ganesh.mandal.entity.NotificationTemplate;
import com.ganesh.mandal.repository.NotificationTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationTemplateService {

    private final NotificationTemplateRepository repository;

    @Transactional
    public NotificationTemplateDTO create(NotificationTemplateDTO dto) {
        return toDTO(repository.save(toEntity(dto)));
    }

    @Transactional
    public NotificationTemplateDTO update(Long id, NotificationTemplateDTO dto) {
        NotificationTemplate t = findEntity(id);
        t.setTemplateName(dto.getTemplateName());
        t.setNotificationType(dto.getNotificationType());
        t.setWhatsappTemplateId(dto.getWhatsappTemplateId());
        t.setEmailSubject(dto.getEmailSubject());
        t.setEmailBody(dto.getEmailBody());
        t.setMessageText(dto.getMessageText());
        t.setStatus(dto.getStatus());
        return toDTO(repository.save(t));
    }

    public NotificationTemplateDTO getById(Long id) {
        return toDTO(findEntity(id));
    }

    public List<NotificationTemplateDTO> getAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NotificationTemplateDTO> getByType(String type) {
        return repository.findByNotificationType(type).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private NotificationTemplate findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NotificationTemplate not found with id: " + id));
    }

    private NotificationTemplateDTO toDTO(NotificationTemplate t) {
        return NotificationTemplateDTO.builder()
                .id(t.getId()).templateName(t.getTemplateName())
                .notificationType(t.getNotificationType())
                .whatsappTemplateId(t.getWhatsappTemplateId())
                .emailSubject(t.getEmailSubject()).emailBody(t.getEmailBody())
                .messageText(t.getMessageText()).status(t.getStatus())
                .createdAt(t.getCreatedAt()).updatedAt(t.getUpdatedAt())
                .build();
    }

    private NotificationTemplate toEntity(NotificationTemplateDTO dto) {
        return NotificationTemplate.builder()
                .templateName(dto.getTemplateName())
                .notificationType(dto.getNotificationType())
                .whatsappTemplateId(dto.getWhatsappTemplateId())
                .emailSubject(dto.getEmailSubject()).emailBody(dto.getEmailBody())
                .messageText(dto.getMessageText())
                .status(dto.getStatus() != null ? dto.getStatus() : "Active")
                .build();
    }
}
