package com.ganesh.mandal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrasadSponsorshipDTO {

    private Long id;

    @NotBlank(message = "Festival year is required")
    private String festivalYear;

    private String festivalDay;

    private LocalDate prasadDate;

    @NotBlank(message = "Prasad name is required")
    private String prasadName;

    private String sponsoredBy;

    private String mobileNumber;

    private String address;

    private String quantity;

    private BigDecimal estimatedCost;

    private BigDecimal donationAmount;

    private String preparedBy;

    private String distributionTime;

    private String status;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
