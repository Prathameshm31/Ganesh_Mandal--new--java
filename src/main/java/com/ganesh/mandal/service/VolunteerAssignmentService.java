package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.VolunteerAssignmentDTO;
import com.ganesh.mandal.entity.Volunteer;
import com.ganesh.mandal.entity.VolunteerAssignment;
import com.ganesh.mandal.repository.EventRepository;
import com.ganesh.mandal.repository.VolunteerAssignmentRepository;
import com.ganesh.mandal.repository.VolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VolunteerAssignmentService {

    private final VolunteerAssignmentRepository assignmentRepository;
    private final VolunteerRepository volunteerRepository;
    private final EventRepository eventRepository;

    @Transactional
    public VolunteerAssignmentDTO create(VolunteerAssignmentDTO dto) {
        VolunteerAssignment a = VolunteerAssignment.builder()
                .volunteer(volunteerRepository.findById(dto.getVolunteerId())
                        .orElseThrow(() -> new EntityNotFoundException("Volunteer not found")))
                .event(eventRepository.findById(dto.getEventId())
                        .orElseThrow(() -> new EntityNotFoundException("Event not found")))
                .role(dto.getRole())
                .dutyDate(dto.getDutyDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .remarks(dto.getRemarks())
                .build();
        return toDTO(assignmentRepository.save(a));
    }

    @Transactional
    public VolunteerAssignmentDTO update(Long id, VolunteerAssignmentDTO dto) {
        VolunteerAssignment a = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));
        if (dto.getVolunteerId() != null && !dto.getVolunteerId().equals(a.getVolunteer().getId())) {
            a.setVolunteer(volunteerRepository.findById(dto.getVolunteerId())
                    .orElseThrow(() -> new EntityNotFoundException("Volunteer not found")));
        }
        if (dto.getEventId() != null && !dto.getEventId().equals(a.getEvent().getId())) {
            a.setEvent(eventRepository.findById(dto.getEventId())
                    .orElseThrow(() -> new EntityNotFoundException("Event not found")));
        }
        if (dto.getRole() != null) a.setRole(dto.getRole());
        if (dto.getDutyDate() != null) a.setDutyDate(dto.getDutyDate());
        if (dto.getStartTime() != null) a.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) a.setEndTime(dto.getEndTime());
        if (dto.getRemarks() != null) a.setRemarks(dto.getRemarks());
        return toDTO(assignmentRepository.save(a));
    }

    @Transactional
    public void delete(Long id) {
        assignmentRepository.deleteById(id);
    }

    public VolunteerAssignmentDTO getById(Long id) {
        return toDTO(assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found")));
    }

    public List<VolunteerAssignmentDTO> getByVolunteer(Long volunteerId) {
        return assignmentRepository.findByVolunteerId(volunteerId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerAssignmentDTO> getByEvent(Long eventId) {
        return assignmentRepository.findByEventId(eventId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerAssignmentDTO> getByDutyDate(LocalDate date) {
        return assignmentRepository.findByDutyDate(date).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerAssignmentDTO> getByFestivalYear(String festivalYear) {
        return assignmentRepository.findByVolunteerFestivalYear(festivalYear).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerAssignmentDTO> getUpcoming() {
        return assignmentRepository.findByDutyDateAfter(LocalDate.now().minusDays(1)).stream()
                .limit(20).map(this::toDTO).collect(Collectors.toList());
    }

    public long countByDutyDate(LocalDate date) {
        return assignmentRepository.countByDutyDate(date);
    }

    public long countUpcoming() {
        return assignmentRepository.countByDutyDateAfter(LocalDate.now());
    }

    private VolunteerAssignmentDTO toDTO(VolunteerAssignment a) {
        return VolunteerAssignmentDTO.builder()
                .id(a.getId())
                .volunteerId(a.getVolunteer().getId())
                .volunteerName(a.getVolunteer().getName())
                .volunteerMobile(a.getVolunteer().getMobile())
                .eventId(a.getEvent().getId())
                .eventName(a.getEvent().getEventName())
                .eventDate(a.getEvent().getDate() != null ? a.getEvent().getDate().toString() : "")
                .role(a.getRole())
                .dutyDate(a.getDutyDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .remarks(a.getRemarks())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
