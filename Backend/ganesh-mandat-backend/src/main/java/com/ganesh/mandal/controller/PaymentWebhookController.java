package com.ganesh.mandal.controller;

import com.ganesh.mandal.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {

    private final PaymentService paymentService;

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(HttpServletRequest request) {
        try {
            String payload = new BufferedReader(request.getReader())
                    .lines().collect(Collectors.joining("\n"));

            String signature = request.getHeader("x-razorpay-signature");
            String webhookSecret = null;

            log.info("Received Razorpay webhook. Payload length: {}", payload.length());

            paymentService.handleWebhook("RAZORPAY", payload, signature, webhookSecret);

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error processing Razorpay webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }

    @PostMapping("/phonepe")
    public ResponseEntity<String> handlePhonePeWebhook(HttpServletRequest request) {
        return handleGenericWebhook(request, "PHONEPE");
    }

    @PostMapping("/cashfree")
    public ResponseEntity<String> handleCashfreeWebhook(HttpServletRequest request) {
        return handleGenericWebhook(request, "CASHFREE");
    }

    @PostMapping("/payu")
    public ResponseEntity<String> handlePayUWebhook(HttpServletRequest request) {
        return handleGenericWebhook(request, "PAYU");
    }

    private ResponseEntity<String> handleGenericWebhook(HttpServletRequest request, String provider) {
        try {
            String payload = new BufferedReader(request.getReader())
                    .lines().collect(Collectors.joining("\n"));

            String signature = request.getHeader("x-webhook-signature");
            if (signature == null) signature = request.getHeader("x-razorpay-signature");

            log.info("Received {} webhook. Payload length: {}", provider, payload.length());
            paymentService.handleWebhook(provider, payload, signature, null);

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error processing {} webhook: {}", provider, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }
}
