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
public class ActivityDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDate date;

    private String time;

    private String venue;

    private String organizer;

    private BigDecimal budget;

    private String status;

    private String bannerImage;

    private String category;

    private LocalDateTime createdAt;
}
