package com.ganesh.mandal.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopDonorDTO {
    private Long memberId;
    private String memberName;
    private BigDecimal totalAmount;
}
