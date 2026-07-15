package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.EventDTO;
import com.ganesh.mandal.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDTO> create(@Valid @RequestBody EventDTO dto) {
        return new ResponseEntity<>(eventService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAll() {
        return ResponseEntity.ok(eventService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> update(@PathVariable Long id, @Valid @RequestBody EventDTO dto) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String festivalDay,
            @RequestParam(required = false) String festivalYear,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(eventService.search(keyword, category, festivalDay, festivalYear, status));
    }

    @GetMapping("/by-year")
    public ResponseEntity<List<EventDTO>> getByYear(@RequestParam String year) {
        return ResponseEntity.ok(eventService.getByFestivalYear(year));
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<EventDTO>> getByDate(@RequestParam String date) {
        return ResponseEntity.ok(eventService.getByDate(LocalDate.parse(date)));
    }
}
