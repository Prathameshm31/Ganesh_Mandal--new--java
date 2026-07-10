package com.ganesh.mandal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VolunteerDTO {
    private Long id;
    @NotBlank private String name;
    @NotBlank private String mobile;
    private String email, address, profilePhoto;
    private LocalDate dateOfBirth;
    private String gender, bloodGroup, emergencyContact, aadhaarNumber;
    private String festivalYear, category, role, skills, experience, availability;
    private LocalDate joiningDate;
    private String status;
    private LocalDateTime createdAt, updatedAt;
}
