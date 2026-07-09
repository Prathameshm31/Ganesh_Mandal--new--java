package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByVolunteerIdAndEventIdAndAttendanceDate(Long volunteerId, Long eventId, LocalDate attendanceDate);
    List<Attendance> findByVolunteerId(Long volunteerId);
    List<Attendance> findByEventId(Long eventId);
    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);
    List<Attendance> findByEventIdAndAttendanceDate(Long eventId, LocalDate date);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.volunteer v WHERE v.festivalYear = :year")
    List<Attendance> findByVolunteerFestivalYear(@Param("year") String festivalYear);

    @Query("SELECT COUNT(a) FROM Attendance a JOIN a.volunteer v WHERE a.status = :status AND v.festivalYear = :year")
    long countByStatusAndVolunteerFestivalYear(@Param("status") String status, @Param("year") String festivalYear);

    long countByStatus(String status);
}
