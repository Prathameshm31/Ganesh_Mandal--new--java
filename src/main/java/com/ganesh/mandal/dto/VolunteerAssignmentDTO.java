package com.ganesh.mandal.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VolunteerAssignmentDTO {
    private Long id;
    private Long volunteerId;
    private String volunteerName, volunteerMobile;
    private Long eventId;
    private String eventName, eventDate;
    private String role;
    private LocalDate dutyDate;
    private String startTime, endTime, remarks;
    private LocalDateTime createdAt;
}
