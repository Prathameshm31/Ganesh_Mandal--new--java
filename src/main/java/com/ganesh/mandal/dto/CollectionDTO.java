package com.ganesh.mandal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionDTO {

    private Long id;

    @NotNull(message = "Member ID is required")
    private Long memberId;

    private String memberName;

    private String memberMobile;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String paymentMode;

    private String transactionId;

    private String receiptNumber;

    private String collectorName;

    private String colony;

    @NotNull(message = "Collection date is required")
    private LocalDate collectionDate;

    private String remarks;

    private LocalDateTime createdAt;
}
