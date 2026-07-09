package com.ganesh.mandal.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColonyWiseDTO {
    private String colonyName;
    private BigDecimal amount;
}
