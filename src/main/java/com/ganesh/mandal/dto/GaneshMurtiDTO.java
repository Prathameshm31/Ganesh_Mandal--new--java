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
public class GaneshMurtiDTO {

    private Long id;

    @NotBlank(message = "Festival year is required")
    private String festivalYear;

    @NotBlank(message = "Murti name is required")
    private String murtiName;

    private String donatedBy;

    private String mobileNumber;

    private String address;

    private String murtiHeight;

    private String murtiType;

    private String artistName;

    private String workshopName;

    private LocalDate installationDate;

    private LocalDate visarjanDate;

    private BigDecimal estimatedCost;

    private String isSponsored;

    private BigDecimal donationAmount;

    private String photoUrl;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
