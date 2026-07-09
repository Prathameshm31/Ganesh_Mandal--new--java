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
@Table(name = "prasad_sponsorship")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrasadSponsorship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Festival year is required")
    @Column(name = "festival_year", nullable = false, length = 10)
    private String festivalYear;

    @Column(name = "festival_day", length = 20)
    private String festivalDay;

    @Column(name = "prasad_date")
    private LocalDate prasadDate;

    @NotBlank(message = "Prasad name is required")
    @Column(name = "prasad_name", nullable = false, length = 200)
    private String prasadName;

    @Column(name = "sponsored_by", length = 200)
    private String sponsoredBy;

    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 50)
    private String quantity;

    @Column(name = "estimated_cost", precision = 12, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "donation_amount", precision = 12, scale = 2)
    private BigDecimal donationAmount;

    @Column(name = "prepared_by", length = 200)
    private String preparedBy;

    @Column(name = "distribution_time", length = 20)
    private String distributionTime;

    @Column(length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
