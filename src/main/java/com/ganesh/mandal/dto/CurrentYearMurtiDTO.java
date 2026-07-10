package com.ganesh.mandal.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentYearMurtiDTO {

    private Long id;
    private String festivalYear;
    private String murtiName;
    private String donatedBy;
    private String artistName;
    private String workshopName;
    private String murtiHeight;
    private String murtiType;
    private LocalDate installationDate;
    private BigDecimal estimatedCost;
    private String photoUrl;
}
