package com.ganesh.mandal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable = false, length = 100)
    private String name;

    @NotBlank @Pattern(regexp = "^[0-9]{10}$")
    @Column(nullable = false, length = 15)
    private String mobile;

    @Column(length = 15)
    private String whatsappNumber;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String colony;

    @Column(length = 100)
    private String area;

    @Column(length = 50)
    private String houseNumber;

    private Integer familyMembers;

    @Column(length = 100)
    private String occupation;

    @Column(length = 500)
    private String profilePhoto;

    @Column(length = 20)
    @Builder.Default
    private String status = "Active";

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDate joinDate;

    @Column(name = "last_year_amount", precision = 10, scale = 2)
    private BigDecimal lastYearAmount;

    @Column(length = 10)
    private String festivalYear;

    @Column(length = 100)
    private String committeeCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
