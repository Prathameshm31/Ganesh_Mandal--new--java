package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.AttendanceDTO;
import com.ganesh.mandal.entity.Attendance;
import com.ganesh.mandal.entity.Volunteer;
import com.ganesh.mandal.entity.Event;
import com.ganesh.mandal.repository.AttendanceRepository;
import com.ganesh.mandal.repository.EventRepository;
import com.ganesh.mandal.repository.VolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final VolunteerRepository volunteerRepository;
    private final EventRepository eventRepository;

    @Transactional
    public AttendanceDTO markAttendance(AttendanceDTO dto) {
        return attendanceRepository.findByVolunteerIdAndEventIdAndAttendanceDate(
                dto.getVolunteerId(), dto.getEventId(), dto.getAttendanceDate())
                .map(existing -> {
                    existing.setStatus(dto.getStatus());
                    existing.setRemarks(dto.getRemarks());
                    return toDTO(attendanceRepository.save(existing));
                })
                .orElseGet(() -> {
                    Attendance a = Attendance.builder()
                            .volunteer(volunteerRepository.findById(dto.getVolunteerId())
                                    .orElseThrow(() -> new EntityNotFoundException("Volunteer not found")))
                            .event(eventRepository.findById(dto.getEventId())
                                    .orElseThrow(() -> new EntityNotFoundException("Event not found")))
                            .attendanceDate(dto.getAttendanceDate())
                            .status(dto.getStatus())
                            .remarks(dto.getRemarks())
                            .build();
                    return toDTO(attendanceRepository.save(a));
                });
    }

    @Transactional
    public void delete(Long id) {
        attendanceRepository.deleteById(id);
    }

    public AttendanceDTO getById(Long id) {
        return toDTO(attendanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found")));
    }

    public List<AttendanceDTO> getByVolunteer(Long volunteerId) {
        return attendanceRepository.findByVolunteerId(volunteerId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AttendanceDTO> getByEvent(Long eventId) {
        return attendanceRepository.findByEventId(eventId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AttendanceDTO> getByDate(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AttendanceDTO> getByEventAndDate(Long eventId, LocalDate date) {
        return attendanceRepository.findByEventIdAndAttendanceDate(eventId, date).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Map<String, Long> getStats(String festivalYear) {
        long total = festivalYear != null ? attendanceRepository.countByStatusAndVolunteerFestivalYear("Present", festivalYear)
                + attendanceRepository.countByStatusAndVolunteerFestivalYear("Absent", festivalYear)
                + attendanceRepository.countByStatusAndVolunteerFestivalYear("Late", festivalYear)
                : attendanceRepository.count();
        return Map.of(
                "present", festivalYear != null ? attendanceRepository.countByStatusAndVolunteerFestivalYear("Present", festivalYear) : 0,
                "absent", festivalYear != null ? attendanceRepository.countByStatusAndVolunteerFestivalYear("Absent", festivalYear) : 0,
                "late", festivalYear != null ? attendanceRepository.countByStatusAndVolunteerFestivalYear("Late", festivalYear) : 0,
                "total", total
        );
    }

    private AttendanceDTO toDTO(Attendance a) {
        return AttendanceDTO.builder()
                .id(a.getId())
                .volunteerId(a.getVolunteer().getId())
                .volunteerName(a.getVolunteer().getName())
                .volunteerMobile(a.getVolunteer().getMobile())
                .volunteerRole(a.getVolunteer().getRole())
                .eventId(a.getEvent().getId())
                .eventName(a.getEvent().getEventName())
                .attendanceDate(a.getAttendanceDate())
                .status(a.getStatus())
                .remarks(a.getRemarks())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
