package com.ganesh.mandal.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long id;
    private String orderId;
    private String paymentId;
    private String transactionId;
    private String receiptNumber;
    private Long userId;
    private String donorName;
    private String donorEmail;
    private String donorMobile;
    private String donationType;
    private BigDecimal amount;
    private String paymentGateway;
    private String paymentMethod;
    private String status;
    private String gatewayResponse;
    private String gatewayStatus;
    private Boolean signatureVerified;
    private Boolean webhookReceived;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private Boolean receiptGenerated;
    private Boolean emailSent;
    private Boolean whatsappSent;
    private LocalDate paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
    private Long collectionId;
}
