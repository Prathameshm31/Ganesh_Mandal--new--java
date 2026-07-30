package com.ganesh.mandal.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDashboardDTO {

    private long totalPayments;
    private long successfulPayments;
    private long pendingPayments;
    private long failedPayments;
    private long refundedPayments;
    private BigDecimal todayCollection;
    private BigDecimal monthlyCollection;
    private List<PaymentDTO> recentPayments;
}
