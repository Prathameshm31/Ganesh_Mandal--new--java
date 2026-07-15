package com.ganesh.mandal.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VolunteerDetailDTO {
    private Long id;
    private String name, mobile, email, address, profilePhoto;
    private String dateOfBirth, gender, bloodGroup, emergencyContact, aadhaarNumber;
    private String festivalYear, category, role, skills, experience, availability;
    private String joiningDate, status;
    private List<VolunteerAssignmentDTO> assignments;
    private List<AttendanceDTO> attendanceRecords;
}
