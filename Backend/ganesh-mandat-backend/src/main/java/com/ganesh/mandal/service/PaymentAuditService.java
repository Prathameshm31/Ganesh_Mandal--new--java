package com.ganesh.mandal.service;

import com.ganesh.mandal.entity.PaymentAuditLog;
import com.ganesh.mandal.repository.PaymentAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentAuditService {

    private final PaymentAuditLogRepository auditLogRepository;

    @Transactional
    public void log(Long paymentId, String action, String previousStatus,
                    String newStatus, String performedBy, String details, String gatewayResponse) {
        PaymentAuditLog audit = PaymentAuditLog.builder()
                .paymentId(paymentId)
                .action(action)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .performedBy(performedBy)
                .details(details)
                .gatewayResponse(gatewayResponse)
                .build();
        auditLogRepository.save(audit);
        log.info("Audit: Payment {} | Action: {} | From: {} → To: {} | By: {}",
                paymentId, action, previousStatus, newStatus, performedBy);
    }

    @Transactional(readOnly = true)
    public List<PaymentAuditLog> getAuditLogs(Long paymentId) {
        return auditLogRepository.findByPaymentIdOrderByCreatedAtDesc(paymentId);
    }
}
