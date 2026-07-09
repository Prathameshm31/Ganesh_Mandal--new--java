package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.NotificationConfigDTO;
import com.ganesh.mandal.service.NotificationConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-config")
@RequiredArgsConstructor
public class NotificationConfigController {

    private final NotificationConfigService service;

    @GetMapping
    public ResponseEntity<NotificationConfigDTO> getConfig() {
        return ResponseEntity.ok(service.getConfig());
    }

    @PutMapping
    public ResponseEntity<NotificationConfigDTO> update(@RequestBody NotificationConfigDTO dto) {
        return ResponseEntity.ok(service.update(dto));
    }
}
