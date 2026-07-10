package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.VolunteerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VolunteerAssignmentRepository extends JpaRepository<VolunteerAssignment, Long> {
    List<VolunteerAssignment> findByVolunteerId(Long volunteerId);
    List<VolunteerAssignment> findByEventId(Long eventId);
    List<VolunteerAssignment> findByDutyDate(LocalDate dutyDate);
    List<VolunteerAssignment> findByVolunteerFestivalYear(String festivalYear);
    List<VolunteerAssignment> findByDutyDateBetween(LocalDate from, LocalDate to);
    List<VolunteerAssignment> findByDutyDateAfter(LocalDate date);
    long countByDutyDate(LocalDate dutyDate);
    long countByDutyDateAfter(LocalDate date);
}
