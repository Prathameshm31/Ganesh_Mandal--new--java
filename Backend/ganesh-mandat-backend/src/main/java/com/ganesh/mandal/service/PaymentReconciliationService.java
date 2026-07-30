package com.ganesh.mandal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentReconciliationService {

    private final PaymentService paymentService;

    @Scheduled(fixedRateString = "${payment.reconciliation.interval-ms:300000}")
    public void reconcilePendingPayments() {
        log.info("Starting payment reconciliation job...");
        try {
            int updated = paymentService.reconcilePendingPayments();
            if (updated > 0) {
                log.info("Reconciliation job completed: {} payments updated", updated);
            }
        } catch (Exception e) {
            log.error("Reconciliation job failed: {}", e.getMessage(), e);
        }
    }
}
