package com.ganesh.mandal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "event_name", nullable = false, length = 200)
    private String eventName;

    @Column(name = "event_category", length = 100)
    private String eventCategory;

    @Column(name = "festival_day", length = 20)
    private String festivalDay;

    @Column(name = "festival_year", length = 10)
    private String festivalYear;

    private LocalDate date;

    @Column(name = "start_time", length = 20)
    private String startTime;

    @Column(name = "end_time", length = 20)
    private String endTime;

    @Column(length = 255)
    private String venue;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String organizer;

    @Column(length = 100)
    private String coordinator;

    @Column(precision = 12, scale = 2)
    private BigDecimal budget;

    @Column(length = 20)
    @Builder.Default
    private String status = "Planned";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
