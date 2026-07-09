package com.ganesh.mandal.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionSummaryDTO {

    private BigDecimal totalAmount;
    private BigDecimal totalCash;
    private BigDecimal totalOnline;
    private Long totalMembersContributed;
}
