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
@Table(name = "ganesh_murti")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GaneshMurti {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "festival_year", nullable = false, length = 10)
    private String festivalYear;

    @NotBlank(message = "Murti name is required")
    @Column(name = "murti_name", nullable = false, length = 200)
    private String murtiName;

    @Column(name = "donated_by", length = 200)
    private String donatedBy;

    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "murti_height", length = 20)
    private String murtiHeight;

    @Column(name = "murti_type", length = 50)
    private String murtiType;

    @Column(name = "artist_name", length = 100)
    private String artistName;

    @Column(name = "workshop_name", length = 200)
    private String workshopName;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "visarjan_date")
    private LocalDate visarjanDate;

    @Column(name = "estimated_cost", precision = 12, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "is_sponsored", length = 5)
    private String isSponsored;

    @Column(name = "donation_amount", precision = 12, scale = 2)
    private BigDecimal donationAmount;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
