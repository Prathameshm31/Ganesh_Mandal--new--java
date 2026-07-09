package com.ganesh.mandal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColonyDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String area;

    private String pincode;

    private Long totalMembers;

    private BigDecimal totalCollection;

    private BigDecimal pendingCollection;
}
