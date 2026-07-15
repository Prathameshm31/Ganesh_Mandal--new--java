package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.VolunteerDTO;
import com.ganesh.mandal.dto.VolunteerDetailDTO;
import com.ganesh.mandal.dto.VolunteerAssignmentDTO;
import com.ganesh.mandal.dto.AttendanceDTO;
import com.ganesh.mandal.entity.Volunteer;
import com.ganesh.mandal.repository.VolunteerAssignmentRepository;
import com.ganesh.mandal.repository.AttendanceRepository;
import com.ganesh.mandal.repository.VolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final VolunteerAssignmentRepository assignmentRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public VolunteerDTO create(VolunteerDTO dto) {
        Volunteer entity = toEntity(dto);
        return toDTO(volunteerRepository.save(entity));
    }

    @Transactional
    public VolunteerDTO update(Long id, VolunteerDTO dto) {
        Volunteer v = findEntity(id);
        v.setName(dto.getName());
        v.setMobile(dto.getMobile());
        v.setEmail(dto.getEmail());
        v.setAddress(dto.getAddress());
        v.setProfilePhoto(dto.getProfilePhoto());
        v.setDateOfBirth(dto.getDateOfBirth());
        v.setGender(dto.getGender());
        v.setBloodGroup(dto.getBloodGroup());
        v.setEmergencyContact(dto.getEmergencyContact());
        v.setAadhaarNumber(dto.getAadhaarNumber());
        v.setFestivalYear(dto.getFestivalYear());
        v.setCategory(dto.getCategory());
        v.setRole(dto.getRole());
        v.setSkills(dto.getSkills());
        v.setExperience(dto.getExperience());
        v.setAvailability(dto.getAvailability());
        v.setJoiningDate(dto.getJoiningDate());
        v.setStatus(dto.getStatus());
        return toDTO(volunteerRepository.save(v));
    }

    @Transactional
    public void delete(Long id) {
        volunteerRepository.deleteById(id);
    }

    public VolunteerDTO getById(Long id) {
        return toDTO(findEntity(id));
    }

    public VolunteerDetailDTO getDetail(Long id) {
        Volunteer v = findEntity(id);
        List<VolunteerAssignmentDTO> assignments = assignmentRepository.findByVolunteerId(id).stream()
                .map(a -> VolunteerAssignmentDTO.builder()
                        .id(a.getId()).volunteerId(a.getVolunteer().getId()).volunteerName(a.getVolunteer().getName())
                        .eventId(a.getEvent().getId()).eventName(a.getEvent().getEventName())
                        .role(a.getRole()).dutyDate(a.getDutyDate())
                        .startTime(a.getStartTime()).endTime(a.getEndTime()).remarks(a.getRemarks())
                        .build())
                .collect(Collectors.toList());
        List<AttendanceDTO> attendance = attendanceRepository.findByVolunteerId(id).stream()
                .map(a -> AttendanceDTO.builder()
                        .id(a.getId()).volunteerId(a.getVolunteer().getId()).volunteerName(a.getVolunteer().getName())
                        .eventId(a.getEvent().getId()).eventName(a.getEvent().getEventName())
                        .attendanceDate(a.getAttendanceDate()).status(a.getStatus()).remarks(a.getRemarks())
                        .build())
                .collect(Collectors.toList());
        return VolunteerDetailDTO.builder()
                .id(v.getId()).name(v.getName()).mobile(v.getMobile()).email(v.getEmail())
                .address(v.getAddress()).profilePhoto(v.getProfilePhoto())
                .dateOfBirth(v.getDateOfBirth() != null ? v.getDateOfBirth().toString() : null)
                .gender(v.getGender()).bloodGroup(v.getBloodGroup())
                .emergencyContact(v.getEmergencyContact()).aadhaarNumber(v.getAadhaarNumber())
                .festivalYear(v.getFestivalYear()).category(v.getCategory()).role(v.getRole())
                .skills(v.getSkills()).experience(v.getExperience()).availability(v.getAvailability())
                .joiningDate(v.getJoiningDate() != null ? v.getJoiningDate().toString() : null)
                .status(v.getStatus()).assignments(assignments).attendanceRecords(attendance)
                .build();
    }

    public List<VolunteerDTO> getAll() {
        return volunteerRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerDTO> search(String keyword, String category, String role, String status, String festivalYear) {
        return volunteerRepository.findAll().stream()
                .filter(v -> festivalYear == null || festivalYear.equals(v.getFestivalYear()))
                .filter(v -> category == null || v.getCategory() != null && v.getCategory().equalsIgnoreCase(category))
                .filter(v -> role == null || v.getRole() != null && v.getRole().equalsIgnoreCase(role))
                .filter(v -> status == null || v.getStatus() != null && v.getStatus().equalsIgnoreCase(status))
                .filter(v -> keyword == null || keyword.isBlank() ||
                        (v.getName() != null && v.getName().toLowerCase().contains(keyword.toLowerCase())) ||
                        (v.getMobile() != null && v.getMobile().contains(keyword)) ||
                        (v.getEmail() != null && v.getEmail().toLowerCase().contains(keyword.toLowerCase())) ||
                        (v.getSkills() != null && v.getSkills().toLowerCase().contains(keyword.toLowerCase())))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<VolunteerDTO> searchFiltered(String keyword, List<String> roles, String category, String role,
                                              String status, String festivalYear, String assignedDate, Integer birthdayMonth) {
        return volunteerRepository.findAll().stream()
                .filter(v -> festivalYear == null || festivalYear.equals(v.getFestivalYear()))
                .filter(v -> category == null || v.getCategory() != null && v.getCategory().equalsIgnoreCase(category))
                .filter(v -> role == null || v.getRole() != null && v.getRole().equalsIgnoreCase(role))
                .filter(v -> status == null || v.getStatus() != null && v.getStatus().equalsIgnoreCase(status))
                .filter(v -> roles == null || roles.isEmpty() || (v.getRole() != null && roles.contains(v.getRole())))
                .filter(v -> birthdayMonth == null || (v.getDateOfBirth() != null && v.getDateOfBirth().getMonthValue() == birthdayMonth))
                .filter(v -> assignedDate == null || assignmentRepository.findByVolunteerId(v.getId()).stream()
                        .anyMatch(a -> a.getDutyDate() != null && a.getDutyDate().toString().equals(assignedDate)))
                .filter(v -> keyword == null || keyword.isBlank() ||
                        (v.getName() != null && v.getName().toLowerCase().contains(keyword.toLowerCase())) ||
                        (v.getMobile() != null && v.getMobile().contains(keyword)) ||
                        (v.getEmail() != null && v.getEmail().toLowerCase().contains(keyword.toLowerCase())) ||
                        (v.getSkills() != null && v.getSkills().toLowerCase().contains(keyword.toLowerCase())))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<VolunteerDTO> getByFestivalYear(String festivalYear) {
        return volunteerRepository.findByFestivalYear(festivalYear).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerDTO> getByCategory(String category) {
        return volunteerRepository.findByCategory(category).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerDTO> getByStatus(String status) {
        return volunteerRepository.findByStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerDTO> getByRoles(List<String> roles) {
        return volunteerRepository.findByRoles(roles).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerDTO> getByFestivalYearAndRoles(String festivalYear, List<String> roles) {
        return volunteerRepository.findByFestivalYearAndRoles(festivalYear, roles).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerDTO> getBirthdaysThisMonth(String festivalYear) {
        int month = LocalDate.now().getMonthValue();
        if (festivalYear != null) {
            return volunteerRepository.findByFestivalYearAndDateOfBirthMonth(festivalYear, month).stream().map(this::toDTO).collect(Collectors.toList());
        }
        return volunteerRepository.findByDateOfBirthMonth(month).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<VolunteerDTO> getByAssignedDate(LocalDate date) {
        Set<Long> ids = assignmentRepository.findByDutyDate(date).stream()
                .map(a -> a.getVolunteer().getId())
                .collect(Collectors.toSet());
        return volunteerRepository.findAllById(ids).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private Volunteer findEntity(Long id) {
        return volunteerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Volunteer not found with id: " + id));
    }

    public VolunteerDTO toDTO(Volunteer v) {
        return VolunteerDTO.builder()
                .id(v.getId()).name(v.getName()).mobile(v.getMobile()).email(v.getEmail())
                .address(v.getAddress()).profilePhoto(v.getProfilePhoto())
                .dateOfBirth(v.getDateOfBirth()).gender(v.getGender())
                .bloodGroup(v.getBloodGroup()).emergencyContact(v.getEmergencyContact())
                .aadhaarNumber(v.getAadhaarNumber()).festivalYear(v.getFestivalYear())
                .category(v.getCategory()).role(v.getRole()).skills(v.getSkills())
                .experience(v.getExperience()).availability(v.getAvailability())
                .joiningDate(v.getJoiningDate()).status(v.getStatus())
                .createdAt(v.getCreatedAt()).updatedAt(v.getUpdatedAt())
                .build();
    }

    public Volunteer toEntity(VolunteerDTO dto) {
        return Volunteer.builder()
                .name(dto.getName()).mobile(dto.getMobile()).email(dto.getEmail())
                .address(dto.getAddress()).profilePhoto(dto.getProfilePhoto())
                .dateOfBirth(dto.getDateOfBirth()).gender(dto.getGender())
                .bloodGroup(dto.getBloodGroup()).emergencyContact(dto.getEmergencyContact())
                .aadhaarNumber(dto.getAadhaarNumber()).festivalYear(dto.getFestivalYear())
                .category(dto.getCategory()).role(dto.getRole()).skills(dto.getSkills())
                .experience(dto.getExperience()).availability(dto.getAvailability())
                .joiningDate(dto.getJoiningDate()).status(dto.getStatus())
                .build();
    }
}
