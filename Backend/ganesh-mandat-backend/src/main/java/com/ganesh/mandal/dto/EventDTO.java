package com.ganesh.mandal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventDTO {
    private Long id;
    @NotBlank private String eventName;
    private String eventCategory, festivalDay, festivalYear;
    private LocalDate date;
    private String startTime, endTime, venue, description;
    private String organizer, coordinator;
    private BigDecimal budget;
    private String status;
    private LocalDateTime createdAt, updatedAt;
}
