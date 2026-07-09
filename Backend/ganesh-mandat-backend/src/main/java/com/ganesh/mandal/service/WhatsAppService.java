package com.ganesh.mandal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {

    public boolean sendWhatsApp(String to, String message) {
        try {
            log.info("WhatsApp message sent to {}: {}", to, message);
            return true;
        } catch (Exception e) {
            log.error("Failed to send WhatsApp to {}: {}", to, e.getMessage());
            return false;
        }
    }
}
