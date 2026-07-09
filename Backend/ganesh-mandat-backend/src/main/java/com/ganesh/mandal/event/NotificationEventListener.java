package com.ganesh.mandal.event;

import com.ganesh.mandal.dto.NotificationRequest;
import com.ganesh.mandal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        NotificationRequest request = event.getRequest();
        notificationService.processNotification(request);
    }
}
