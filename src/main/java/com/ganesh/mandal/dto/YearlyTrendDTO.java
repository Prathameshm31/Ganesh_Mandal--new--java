package com.ganesh.mandal.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearlyTrendDTO {
    private Integer year;
    private BigDecimal amount;
}
