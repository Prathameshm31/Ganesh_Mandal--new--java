package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.VolunteerAssignmentDTO;
import com.ganesh.mandal.service.VolunteerAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class VolunteerAssignmentController {

    private final VolunteerAssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<VolunteerAssignmentDTO> create(@RequestBody VolunteerAssignmentDTO dto) {
        return new ResponseEntity<>(assignmentService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerAssignmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerAssignmentDTO> update(@PathVariable Long id, @RequestBody VolunteerAssignmentDTO dto) {
        return ResponseEntity.ok(assignmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-volunteer/{volunteerId}")
    public ResponseEntity<List<VolunteerAssignmentDTO>> getByVolunteer(@PathVariable Long volunteerId) {
        return ResponseEntity.ok(assignmentService.getByVolunteer(volunteerId));
    }

    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<VolunteerAssignmentDTO>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(assignmentService.getByEvent(eventId));
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<VolunteerAssignmentDTO>> getByDate(@RequestParam String date) {
        return ResponseEntity.ok(assignmentService.getByDutyDate(LocalDate.parse(date)));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<VolunteerAssignmentDTO>> getUpcoming() {
        return ResponseEntity.ok(assignmentService.getUpcoming());
    }
}
