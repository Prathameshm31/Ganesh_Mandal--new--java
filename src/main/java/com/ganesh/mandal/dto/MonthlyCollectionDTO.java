package com.ganesh.mandal.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyCollectionDTO {
    private String month;
    private BigDecimal amount;
}
