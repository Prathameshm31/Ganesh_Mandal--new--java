package com.ganesh.mandal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerificationRequest {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    private String signature;

    private String gatewayResponse;
}
