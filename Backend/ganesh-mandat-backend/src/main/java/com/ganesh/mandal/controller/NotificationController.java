package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.NotificationDashboardDTO;
import com.ganesh.mandal.dto.NotificationHistoryDTO;
import com.ganesh.mandal.dto.NotificationRequest;
import com.ganesh.mandal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody NotificationRequest request) {
        notificationService.sendAsync(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reminder")
    public ResponseEntity<Void> sendReminder(@RequestBody NotificationRequest request) {
        notificationService.sendAsync(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<NotificationDashboardDTO> getDashboard() {
        return ResponseEntity.ok(notificationService.getDashboard());
    }

    @GetMapping("/history")
    public ResponseEntity<List<NotificationHistoryDTO>> getHistory(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ResponseEntity.ok(notificationService.getHistory(status, channel, eventId, userId, dateFrom, dateTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationHistoryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @PostMapping("/{notificationId}/resend")
    public ResponseEntity<NotificationHistoryDTO> resend(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.resend(notificationId));
    }
}
