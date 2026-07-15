package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.VolunteerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface VolunteerAssignmentRepository extends JpaRepository<VolunteerAssignment, Long> {
    List<VolunteerAssignment> findByVolunteerId(Long volunteerId);
    List<VolunteerAssignment> findByEventId(Long eventId);
    List<VolunteerAssignment> findByDutyDate(LocalDate dutyDate);
    List<VolunteerAssignment> findByVolunteerFestivalYear(String festivalYear);
    List<VolunteerAssignment> findByDutyDateBetween(LocalDate from, LocalDate to);
    List<VolunteerAssignment> findByDutyDateAfter(LocalDate date);
    long countByDutyDate(LocalDate dutyDate);
    long countByDutyDateAfter(LocalDate date);

    @Query("SELECT a.volunteer.id FROM VolunteerAssignment a WHERE a.dutyDate = :dutyDate")
    Set<Long> findVolunteerIdsByDutyDate(@Param("dutyDate") LocalDate dutyDate);
}
