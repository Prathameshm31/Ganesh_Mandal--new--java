package com.ganesh.mandal.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderResponse {

    private Long paymentId;
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String gatewayOrderId;
    private String gatewayKeyId;
    private String gatewaySecret;
    private String notes;
    private String status;
}
