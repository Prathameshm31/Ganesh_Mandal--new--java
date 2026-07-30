package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.*;
import com.ganesh.mandal.entity.*;
import com.ganesh.mandal.gateway.PaymentGatewayFactory;
import com.ganesh.mandal.gateway.PaymentGatewayProvider;
import com.ganesh.mandal.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAuditService auditService;
    private final PaymentGatewayFactory gatewayFactory;
    private final CollectionRepository collectionRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthorizationService authorizationService;

    // ── Create Payment Order ──────────────────────────────────────

    @Transactional
    public PaymentOrderResponse createPaymentOrder(PaymentOrderRequest request, String performedBy) {
        DonationType donationType = DonationType.GENERAL;
        if (request.getDonationType() != null) {
            try {
                donationType = DonationType.valueOf(request.getDonationType().toUpperCase());
            } catch (IllegalArgumentException e) {
                donationType = DonationType.GENERAL;
            }
        }

        PaymentGateway gateway = PaymentGateway.RAZORPAY;
        if (request.getPaymentGateway() != null) {
            try {
                gateway = PaymentGateway.valueOf(request.getPaymentGateway().toUpperCase());
            } catch (IllegalArgumentException e) {
                gateway = PaymentGateway.RAZORPAY;
            }
        }

        Payment payment = Payment.builder()
                .donorName(request.getDonorName())
                .donorEmail(request.getDonorEmail())
                .donorMobile(request.getDonorMobile())
                .donationType(donationType)
                .amount(request.getAmount())
                .paymentGateway(gateway)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDate.now())
                .notes(request.getNotes())
                .build();
        payment = paymentRepository.save(payment);

        String receiptNumber = "ORD-" + System.currentTimeMillis();
        String notes = request.getNotes() != null ? request.getNotes() : "Donation for " + donationType;

        PaymentGatewayProvider provider = gatewayFactory.getProvider(gateway);
        Map<String, Object> orderData = provider.createOrder(request.getAmount(), receiptNumber, notes);

        String gatewayOrderId = (String) orderData.get("id");
        payment.setOrderId("PAY-" + payment.getId() + "-" + System.currentTimeMillis());
        payment.setReceiptNumber(receiptNumber);

        if (gatewayOrderId != null) {
            payment.setTransactionId(gatewayOrderId);
            payment.setGatewayResponse(orderData.toString());
        }
        payment = paymentRepository.save(payment);

        auditService.log(payment.getId(), "PAYMENT_CREATED", null, "PENDING",
                performedBy, "Order created for amount: " + request.getAmount(), orderData.toString());

        return PaymentOrderResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(request.getAmount())
                .currency("INR")
                .gatewayOrderId(gatewayOrderId)
                .gatewayKeyId((String) orderData.get("key_id"))
                .status("created")
                .notes(notes)
                .build();
    }

    // ── Verify Payment (after frontend returns) ───────────────────

    @Transactional
    public PaymentDTO verifyPayment(PaymentVerificationRequest request, String performedBy) {
        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Payment order not found: " + request.getOrderId()));

        String previousStatus = payment.getStatus().name();
        PaymentGatewayProvider provider = gatewayFactory.getProvider(payment.getPaymentGateway());

        Map<String, Object> verification = provider.verifyPayment(
                request.getOrderId(), request.getPaymentId(), request.getSignature());

        boolean signatureValid = verification.containsKey("signature_valid")
                ? (boolean) verification.get("signature_valid") : false;

        payment.setPaymentId(request.getPaymentId());
        payment.setSignature(request.getSignature());
        payment.setSignatureVerified(signatureValid);
        payment.setGatewayResponse(request.getGatewayResponse());

        if (signatureValid) {
            payment.setStatus(PaymentStatus.SUCCESS);
            completeSuccessfulPayment(payment, performedBy);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        payment = paymentRepository.save(payment);

        auditService.log(payment.getId(), "VERIFICATION_PERFORMED", previousStatus, payment.getStatus().name(),
                performedBy, "Signature verification: " + (signatureValid ? "VALID" : "INVALID"),
                verification.toString());

        return toDTO(payment);
    }

    // ── Verify Payment from Gateway API ───────────────────────────

    @Transactional
    public PaymentDTO verifyWithGateway(Long paymentId, String performedBy) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        String previousStatus = payment.getStatus().name();
        PaymentGatewayProvider provider = gatewayFactory.getProvider(payment.getPaymentGateway());
        String paymentIdToCheck = payment.getPaymentId();

        if (paymentIdToCheck == null) {
            auditService.log(payment.getId(), "VERIFY_FAILED", previousStatus, previousStatus,
                    performedBy, "No payment ID available to verify with gateway", null);
            throw new RuntimeException("Cannot verify: no payment ID from gateway");
        }

        Map<String, Object> gatewayStatus = provider.fetchPaymentStatus(paymentIdToCheck);
        String gatewayPaymentStatus = (String) gatewayStatus.get("status");
        String rawResponse = (String) gatewayStatus.getOrDefault("raw_response", gatewayStatus.toString());

        payment.setGatewayResponse(rawResponse);
        payment.setGatewayStatus(gatewayPaymentStatus);

        boolean isSuccess = "captured".equals(gatewayPaymentStatus) || "authorized".equals(gatewayPaymentStatus);

        if (isSuccess && payment.getStatus() != PaymentStatus.SUCCESS) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setVerifiedBy(performedBy);
            payment.setVerifiedAt(LocalDateTime.now());
            completeSuccessfulPayment(payment, performedBy);
        } else if (!isSuccess && payment.getStatus() == PaymentStatus.PENDING || payment.getStatus() == PaymentStatus.PROCESSING) {
            String errorReason = (String) gatewayStatus.get("error_reason");
            if ("failed".equals(gatewayPaymentStatus)) {
                payment.setStatus(PaymentStatus.FAILED);
            } else if ("cancelled".equals(gatewayPaymentStatus)) {
                payment.setStatus(PaymentStatus.CANCELLED);
            }
        }

        payment = paymentRepository.save(payment);

        auditService.log(payment.getId(), "VERIFY_WITH_GATEWAY", previousStatus, payment.getStatus().name(),
                performedBy, "Gateway status: " + gatewayPaymentStatus + " | Success: " + isSuccess, rawResponse);

        return toDTO(payment);
    }

    // ── Webhook Handler ─────────────────────────────────────────────

    @Transactional
    public void handleWebhook(String providerName, String payload, String signature, String webhookSecret) {
        PaymentGatewayProvider provider = gatewayFactory.getProvider(providerName);

        boolean valid = provider.verifyWebhook(payload, signature, webhookSecret);
        if (!valid) {
            log.warn("Webhook signature verification failed for {}", providerName);
            return;
        }

        try {
            com.fasterxml.jackson.databind.JsonNode event =
                    new com.fasterxml.jackson.databind.ObjectMapper().readTree(payload);
            String eventType = event.has("event") ? event.get("event").asText() : "";

            if (!eventType.contains("payment")) {
                log.info("Ignoring non-payment webhook event: {}", eventType);
                return;
            }

            com.fasterxml.jackson.databind.JsonNode paymentNode = event.has("payload")
                    ? event.get("payload").get("payment").get("entity") : event;

            String paymentId = paymentNode.has("id") ? paymentNode.get("id").asText() : null;
            String webhookStatus = paymentNode.has("status") ? paymentNode.get("status").asText() : null;

            if (paymentId == null) {
                log.warn("Webhook missing payment ID");
                return;
            }

            Optional<Payment> existingPayment = paymentRepository.findByPaymentId(paymentId);
            Payment payment;

            if (existingPayment.isPresent()) {
                payment = existingPayment.get();
            } else {
                log.info("Webhook received for unknown payment, creating placeholder: {}", paymentId);
                payment = Payment.builder()
                        .paymentId(paymentId)
                        .status(PaymentStatus.PENDING)
                        .paymentGateway(PaymentGateway.valueOf(providerName.toUpperCase()))
                        .paymentDate(LocalDate.now())
                        .build();
            }

            payment.setWebhookReceived(true);
            payment.setWebhookPayload(payload);

            String previousStatus = payment.getStatus().name();

            if ("captured".equals(webhookStatus) && payment.getStatus() != PaymentStatus.SUCCESS) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setGatewayStatus(webhookStatus);
                completeSuccessfulPayment(payment, "WEBHOOK");
            } else if ("failed".equals(webhookStatus)) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setGatewayStatus(webhookStatus);
            } else {
                payment.setGatewayStatus(webhookStatus);
            }

            paymentRepository.save(payment);
            auditService.log(payment.getId(), "WEBHOOK_RECEIVED", previousStatus, payment.getStatus().name(),
                    "WEBHOOK", "Event: " + eventType + " | Gateway status: " + webhookStatus, payload);

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
        }
    }

    // ── Reconciliation Job ──────────────────────────────────────────

    @Transactional
    public int reconcilePendingPayments() {
        List<Payment> pendingPayments = paymentRepository.findByStatusIn(
                List.of(PaymentStatus.PENDING, PaymentStatus.PROCESSING));

        int updated = 0;
        for (Payment payment : pendingPayments) {
            try {
                if (payment.getPaymentId() == null) continue;

                PaymentGatewayProvider provider = gatewayFactory.getProvider(payment.getPaymentGateway());
                Map<String, Object> gatewayStatus = provider.fetchPaymentStatus(payment.getPaymentId());
                String gatewayPaymentStatus = (String) gatewayStatus.get("status");

                boolean isSuccess = "captured".equals(gatewayPaymentStatus) || "authorized".equals(gatewayPaymentStatus);

                if (isSuccess) {
                    String previousStatus = payment.getStatus().name();
                    payment.setStatus(PaymentStatus.SUCCESS);
                    payment.setGatewayResponse(gatewayStatus.toString());
                    payment.setGatewayStatus(gatewayPaymentStatus);
                    paymentRepository.save(payment);
                    completeSuccessfulPayment(payment, "RECONCILIATION_JOB");
                    auditService.log(payment.getId(), "RECONCILIATION_UPDATED", previousStatus, "SUCCESS",
                            "RECONCILIATION_JOB", "Auto-reconciled from gateway: " + gatewayPaymentStatus,
                            gatewayStatus.toString());
                    updated++;
                } else if ("failed".equals(gatewayPaymentStatus) || "cancelled".equals(gatewayPaymentStatus)) {
                    String previousStatus = payment.getStatus().name();
                    PaymentStatus newStatus = "failed".equals(gatewayPaymentStatus) ? PaymentStatus.FAILED : PaymentStatus.CANCELLED;
                    payment.setStatus(newStatus);
                    payment.setGatewayStatus(gatewayPaymentStatus);
                    paymentRepository.save(payment);
                    auditService.log(payment.getId(), "RECONCILIATION_UPDATED", previousStatus, newStatus.name(),
                            "RECONCILIATION_JOB", "Auto-updated from gateway: " + gatewayPaymentStatus,
                            gatewayStatus.toString());
                    updated++;
                }
            } catch (Exception e) {
                log.error("Reconciliation failed for payment {}: {}", payment.getId(), e.getMessage());
            }
        }
        if (updated > 0) {
            log.info("Reconciliation completed: {} payments updated", updated);
        }
        return updated;
    }

    // ── Helper: Complete Successful Payment ────────────────────────

    private void completeSuccessfulPayment(Payment payment, String performedBy) {
        if (payment.getReceiptGenerated() == null || !payment.getReceiptGenerated()) {
            String receiptNumber = "RCP-" + System.currentTimeMillis();
            payment.setReceiptNumber(receiptNumber);
            payment.setPaymentDate(LocalDate.now());
            payment.setVerifiedBy(performedBy);
            payment.setVerifiedAt(LocalDateTime.now());

            createCollectionFromPayment(payment);
            sendPaymentNotifications(payment);

            payment.setReceiptGenerated(true);
            payment.setEmailSent(true);
            payment.setWhatsappSent(true);

            auditService.log(payment.getId(), "RECEIPT_GENERATED", null, null,
                    performedBy, "Receipt: " + receiptNumber, null);
            auditService.log(payment.getId(), "NOTIFICATION_SENT", null, null,
                    performedBy, "Email and WhatsApp notifications sent", null);
        }
    }

    private void createCollectionFromPayment(Payment payment) {
        Member member = null;
        if (payment.getDonorMobile() != null) {
            member = memberRepository.findByMobile(payment.getDonorMobile()).orElse(null);
        }
        if (member == null && payment.getDonorEmail() != null) {
            member = memberRepository.findByEmail(payment.getDonorEmail()).orElse(null);
        }
        if (member == null && payment.getUser() != null) {
            member = memberRepository.findByUserId(payment.getUser().getId()).orElse(null);
        }

        if (member != null) {
            com.ganesh.mandal.entity.Collection coll = com.ganesh.mandal.entity.Collection.builder()
                    .member(member)
                    .amount(payment.getAmount())
                    .paymentMode(PaymentMode.ONLINE)
                    .receiptNumber(payment.getReceiptNumber())
                    .transactionId(payment.getPaymentId() != null ? payment.getPaymentId() : payment.getTransactionId())
                    .collectorName("Online Payment")
                    .colony(member.getColony())
                    .collectionDate(LocalDate.now())
                    .remarks("Auto-generated from online payment | Type: " + payment.getDonationType() +
                            " | Gateway: " + payment.getPaymentGateway() +
                            " | Order: " + payment.getOrderId())
                    .build();
            coll = collectionRepository.save(coll);
            payment.setCollection(coll);
        }
    }

    private void sendPaymentNotifications(Payment payment) {
        String email = payment.getDonorEmail();
        String mobile = payment.getDonorMobile();

        NotificationRequest emailReq = NotificationRequest.builder()
                .notificationType("Donation")
                .receivers(email != null ? List.of(email) : List.of())
                .channels(List.of("Email"))
                .donorName(payment.getDonorName())
                .amount(payment.getAmount().toString())
                .paymentMode("Online (" + payment.getPaymentGateway() + ")")
                .transactionId(payment.getPaymentId() != null ? payment.getPaymentId() : payment.getTransactionId())
                .date(payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : LocalDate.now().toString())
                .receiptNumber(payment.getReceiptNumber())
                .email(email)
                .build();

        if (email != null && !email.isBlank()) {
            eventPublisher.publishEvent(
                    new com.ganesh.mandal.event.NotificationEvent(this, emailReq));
        }

        if (mobile != null && !mobile.isBlank()) {
            NotificationRequest whatsAppReq = NotificationRequest.builder()
                    .notificationType("Donation")
                    .receivers(List.of(mobile))
                    .channels(List.of("WhatsApp"))
                    .donorName(payment.getDonorName())
                    .amount(payment.getAmount().toString())
                    .paymentMode("Online (" + payment.getPaymentGateway() + ")")
                    .transactionId(payment.getPaymentId() != null ? payment.getPaymentId() : payment.getTransactionId())
                    .date(payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : LocalDate.now().toString())
                    .receiptNumber(payment.getReceiptNumber())
                    .build();
            eventPublisher.publishEvent(
                    new com.ganesh.mandal.event.NotificationEvent(this, whatsAppReq));
        }

        NotificationRequest adminReq = NotificationRequest.builder()
                .notificationType("Donation_Admin")
                .receivers(List.of("admin"))
                .channels(List.of("Admin"))
                .donorName(payment.getDonorName())
                .amount(payment.getAmount().toString())
                .paymentMode("Online (" + payment.getPaymentGateway() + ")")
                .transactionId(payment.getPaymentId() != null ? payment.getPaymentId() : payment.getTransactionId())
                .date(payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : LocalDate.now().toString())
                .receiptNumber(payment.getReceiptNumber())
                .build();
        eventPublisher.publishEvent(new com.ganesh.mandal.event.NotificationEvent(this, adminReq));
    }

    // ── CRUD Operations ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
        return toDTO(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentDTO> searchPayments(String status, LocalDate startDate, LocalDate endDate,
                                            String donationType, String paymentGateway,
                                            String donorName, String mobile, String email,
                                            String searchTerm,
                                            int page, int size, String sortBy, String sortOrder) {
        Specification<Payment> spec = Specification.where(null);

        if (status != null && !status.isBlank()) {
            PaymentStatus ps = PaymentStatus.valueOf(status.toUpperCase());
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), ps));
        }
        if (startDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("paymentDate"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("paymentDate"), endDate));
        }
        if (donationType != null && !donationType.isBlank()) {
            DonationType dt = DonationType.valueOf(donationType.toUpperCase());
            spec = spec.and((root, query, cb) -> cb.equal(root.get("donationType"), dt));
        }
        if (paymentGateway != null && !paymentGateway.isBlank()) {
            PaymentGateway pg = PaymentGateway.valueOf(paymentGateway.toUpperCase());
            spec = spec.and((root, query, cb) -> cb.equal(root.get("paymentGateway"), pg));
        }
        if (donorName != null && !donorName.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("donorName")), "%" + donorName.toLowerCase() + "%"));
        }
        if (mobile != null && !mobile.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("donorMobile"), "%" + mobile + "%"));
        }
        if (email != null && !email.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("donorEmail")), "%" + email.toLowerCase() + "%"));
        }
        if (searchTerm != null && !searchTerm.isBlank()) {
            String term = searchTerm.toLowerCase();
            spec = spec.and((root, query, cb) -> {
                Predicate p1 = cb.like(cb.lower(root.get("orderId")), "%" + term + "%");
                Predicate p2 = cb.like(cb.lower(root.get("paymentId")), "%" + term + "%");
                Predicate p3 = cb.like(cb.lower(root.get("transactionId")), "%" + term + "%");
                Predicate p4 = cb.like(cb.lower(root.get("donorName")), "%" + term + "%");
                Predicate p5 = cb.like(root.get("donorMobile"), "%" + searchTerm + "%");
                return cb.or(p1, p2, p3, p4, p5);
            });
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return paymentRepository.findAll(spec, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public PaymentDashboardDTO getDashboard() {
        long total = paymentRepository.count();
        long successful = paymentRepository.countByStatus(PaymentStatus.SUCCESS);
        long pending = paymentRepository.countByStatus(PaymentStatus.PENDING)
                + paymentRepository.countByStatus(PaymentStatus.PROCESSING);
        long failed = paymentRepository.countByStatus(PaymentStatus.FAILED);
        long refunded = paymentRepository.countByStatus(PaymentStatus.REFUNDED);

        BigDecimal today = paymentRepository.sumSuccessfulByDate(LocalDate.now());
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        BigDecimal monthly = paymentRepository.sumSuccessfulBetween(startOfMonth, startOfMonth.plusMonths(1));

        List<PaymentDTO> recent = paymentRepository.findTop10ByOrderByCreatedAtDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());

        return PaymentDashboardDTO.builder()
                .totalPayments(total)
                .successfulPayments(successful)
                .pendingPayments(pending)
                .failedPayments(failed)
                .refundedPayments(refunded)
                .todayCollection(today)
                .monthlyCollection(monthly)
                .recentPayments(recent)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PaymentReportDTO> getReportByGateway() {
        List<Payment> successPayments = paymentRepository.findByStatus(PaymentStatus.SUCCESS);
        Map<String, List<Payment>> grouped = successPayments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentGateway() != null ? p.getPaymentGateway().name() : "UNKNOWN"));

        List<PaymentReportDTO> results = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        long totalCount = 0;

        for (Map.Entry<String, List<Payment>> entry : grouped.entrySet()) {
            BigDecimal sum = entry.getValue().stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long count = entry.getValue().size();
            totalAmount = totalAmount.add(sum);
            totalCount += count;
            results.add(PaymentReportDTO.builder()
                    .label(entry.getKey())
                    .amount(sum)
                    .count(count)
                    .build());
        }

        results.add(0, PaymentReportDTO.builder()
                .label("TOTAL")
                .amount(totalAmount)
                .count(totalCount)
                .build());

        return results;
    }

    @Transactional(readOnly = true)
    public List<PaymentAuditLog> getAuditLogs(Long paymentId) {
        return auditService.getAuditLogs(paymentId);
    }

    @Transactional(readOnly = true)
    public GatewayConfigDTO getGatewayConfig() {
        PaymentGatewayProvider provider = gatewayFactory.getDefaultProvider();
        return provider.getConfig();
    }

    // ── DTO Conversion ─────────────────────────────────────────────

    private PaymentDTO toDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .paymentId(payment.getPaymentId())
                .transactionId(payment.getTransactionId())
                .receiptNumber(payment.getReceiptNumber())
                .userId(payment.getUser() != null ? payment.getUser().getId() : null)
                .donorName(payment.getDonorName())
                .donorEmail(payment.getDonorEmail())
                .donorMobile(payment.getDonorMobile())
                .donationType(payment.getDonationType() != null ? payment.getDonationType().name() : null)
                .amount(payment.getAmount())
                .paymentGateway(payment.getPaymentGateway() != null ? payment.getPaymentGateway().name() : null)
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus().name())
                .gatewayResponse(payment.getGatewayResponse())
                .gatewayStatus(payment.getGatewayStatus())
                .signatureVerified(payment.getSignatureVerified())
                .webhookReceived(payment.getWebhookReceived())
                .verifiedBy(payment.getVerifiedBy())
                .verifiedAt(payment.getVerifiedAt())
                .receiptGenerated(payment.getReceiptGenerated())
                .emailSent(payment.getEmailSent())
                .whatsappSent(payment.getWhatsappSent())
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .notes(payment.getNotes())
                .collectionId(payment.getCollection() != null ? payment.getCollection().getId() : null)
                .build();
    }
}
