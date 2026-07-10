package com.ganesh.mandal.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceDTO {
    private Long id;
    private Long volunteerId;
    private String volunteerName, volunteerMobile, volunteerRole;
    private Long eventId;
    private String eventName;
    private LocalDate attendanceDate;
    private String status;
    private String remarks;
    private LocalDateTime createdAt;
}
