package com.ganesh.mandal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobile;

    @Pattern(regexp = "^[0-9]{10}$", message = "WhatsApp number must be 10 digits")
    private String whatsappNumber;

    @Email(message = "Email must be valid")
    private String email;

    private String address;

    private String colony;

    private String area;

    private String houseNumber;

    @Min(value = 1, message = "Family members must be at least 1")
    private Integer familyMembers;

    private String occupation;

    private String profilePhoto;

    private String status;

    private String notes;

    private LocalDate joinDate;

    private BigDecimal lastYearAmount;

    private String festivalYear;

    private String committeeCategory;

    private Long userId;

    private Long roleId;

    private String username;

    private String password;

    private List<String> roles;

    private LocalDateTime createdAt;
}
