package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.VolunteerDTO;
import com.ganesh.mandal.dto.VolunteerDetailDTO;
import com.ganesh.mandal.service.VolunteerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;

    @PostMapping
    public ResponseEntity<VolunteerDTO> create(@Valid @RequestBody VolunteerDTO dto) {
        return new ResponseEntity<>(volunteerService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VolunteerDTO>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String festivalYear) {
        if (category != null) return ResponseEntity.ok(volunteerService.getByCategory(category));
        if (role != null) return ResponseEntity.ok(volunteerService.search(null, null, role, null, festivalYear));
        if (festivalYear != null) return ResponseEntity.ok(volunteerService.getByFestivalYear(festivalYear));
        return ResponseEntity.ok(volunteerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.getById(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<VolunteerDetailDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.getDetail(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerDTO> update(@PathVariable Long id, @Valid @RequestBody VolunteerDTO dto) {
        return ResponseEntity.ok(volunteerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        volunteerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<VolunteerDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String festivalYear,
            @RequestParam(required = false) String roles,
            @RequestParam(required = false) String assignedDate,
            @RequestParam(required = false) Integer birthdayMonth) {
        List<String> roleList = roles != null ? List.of(roles.split(",")) : null;
        return ResponseEntity.ok(volunteerService.searchFiltered(keyword, roleList, category, role,
                status, festivalYear, assignedDate, birthdayMonth));
    }

    @GetMapping("/by-year")
    public ResponseEntity<List<VolunteerDTO>> getByYear(@RequestParam String year) {
        return ResponseEntity.ok(volunteerService.getByFestivalYear(year));
    }

    @GetMapping("/by-roles")
    public ResponseEntity<List<VolunteerDTO>> getByRoles(
            @RequestParam String roles,
            @RequestParam(required = false) String festivalYear) {
        List<String> roleList = List.of(roles.split(","));
        if (festivalYear != null) {
            return ResponseEntity.ok(volunteerService.getByFestivalYearAndRoles(festivalYear, roleList));
        }
        return ResponseEntity.ok(volunteerService.getByRoles(roleList));
    }

    @GetMapping("/by-assigned-date")
    public ResponseEntity<List<VolunteerDTO>> getByAssignedDate(@RequestParam String date) {
        return ResponseEntity.ok(volunteerService.getByAssignedDate(LocalDate.parse(date)));
    }

    @GetMapping("/birthdays")
    public ResponseEntity<List<VolunteerDTO>> getBirthdays(@RequestParam(required = false) String festivalYear) {
        return ResponseEntity.ok(volunteerService.getBirthdaysThisMonth(festivalYear));
    }
}
