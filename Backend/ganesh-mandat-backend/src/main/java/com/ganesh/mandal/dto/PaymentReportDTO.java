package com.ganesh.mandal.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReportDTO {

    private String label;
    private BigDecimal amount;
    private long count;
}
