package com.ganesh.mandal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", length = 100, unique = true)
    private String orderId;

    @Column(name = "payment_id", length = 100)
    private String paymentId;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "donor_name", length = 100)
    private String donorName;

    @Column(name = "donor_email", length = 100)
    private String donorEmail;

    @Column(name = "donor_mobile", length = 20)
    private String donorMobile;

    @Enumerated(EnumType.STRING)
    @Column(name = "donation_type", length = 30)
    private DonationType donationType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_gateway", length = 20)
    private PaymentGateway paymentGateway;

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "gateway_status", length = 50)
    private String gatewayStatus;

    @Column(name = "signature", length = 255)
    private String signature;

    @Column(name = "signature_verified")
    private Boolean signatureVerified;

    @Column(name = "webhook_received")
    private Boolean webhookReceived;

    @Column(name = "webhook_payload", columnDefinition = "TEXT")
    private String webhookPayload;

    @Column(name = "verified_by", length = 100)
    private String verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "receipt_generated")
    private Boolean receiptGenerated;

    @Column(name = "email_sent")
    private Boolean emailSent;

    @Column(name = "whatsapp_sent")
    private Boolean whatsappSent;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(length = 255)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = PaymentStatus.PENDING;
        if (signatureVerified == null) signatureVerified = false;
        if (webhookReceived == null) webhookReceived = false;
        if (receiptGenerated == null) receiptGenerated = false;
        if (emailSent == null) emailSent = false;
        if (whatsappSent == null) whatsappSent = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
