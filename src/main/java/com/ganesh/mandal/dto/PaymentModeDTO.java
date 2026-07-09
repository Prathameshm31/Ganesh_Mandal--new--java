package com.ganesh.mandal.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentModeDTO {
    private String mode;
    private BigDecimal amount;
    private Integer percentage;
}
