package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.EventDTO;
import com.ganesh.mandal.entity.Event;
import com.ganesh.mandal.repository.EventRepository;
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
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventDTO create(EventDTO dto) {
        Event entity = toEntity(dto);
        return toDTO(eventRepository.save(entity));
    }

    @Transactional
    public EventDTO update(Long id, EventDTO dto) {
        Event e = findEntity(id);
        e.setEventName(dto.getEventName());
        e.setEventCategory(dto.getEventCategory());
        e.setFestivalDay(dto.getFestivalDay());
        e.setFestivalYear(dto.getFestivalYear());
        e.setDate(dto.getDate());
        e.setStartTime(dto.getStartTime());
        e.setEndTime(dto.getEndTime());
        e.setVenue(dto.getVenue());
        e.setDescription(dto.getDescription());
        e.setOrganizer(dto.getOrganizer());
        e.setCoordinator(dto.getCoordinator());
        e.setBudget(dto.getBudget());
        e.setStatus(dto.getStatus());
        return toDTO(eventRepository.save(e));
    }

    @Transactional
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    public EventDTO getById(Long id) {
        return toDTO(findEntity(id));
    }

    public List<EventDTO> getAll() {
        return eventRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventDTO> search(String keyword, String category, String festivalDay, String festivalYear, String status) {
        return eventRepository.findAll().stream()
                .filter(e -> festivalYear == null || festivalYear.equals(e.getFestivalYear()))
                .filter(e -> category == null || e.getEventCategory() != null && e.getEventCategory().equalsIgnoreCase(category))
                .filter(e -> festivalDay == null || e.getFestivalDay() != null && e.getFestivalDay().equalsIgnoreCase(festivalDay))
                .filter(e -> status == null || e.getStatus() != null && e.getStatus().equalsIgnoreCase(status))
                .filter(e -> keyword == null || keyword.isBlank() ||
                        (e.getEventName() != null && e.getEventName().toLowerCase().contains(keyword.toLowerCase())) ||
                        (e.getOrganizer() != null && e.getOrganizer().toLowerCase().contains(keyword.toLowerCase())) ||
                        (e.getVenue() != null && e.getVenue().toLowerCase().contains(keyword.toLowerCase())))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getByFestivalYear(String festivalYear) {
        return eventRepository.findByFestivalYear(festivalYear).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventDTO> getByDate(LocalDate date) {
        return eventRepository.findByDate(date).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private Event findEntity(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
    }

    public EventDTO toDTO(Event e) {
        return EventDTO.builder()
                .id(e.getId()).eventName(e.getEventName()).eventCategory(e.getEventCategory())
                .festivalDay(e.getFestivalDay()).festivalYear(e.getFestivalYear())
                .date(e.getDate()).startTime(e.getStartTime()).endTime(e.getEndTime())
                .venue(e.getVenue()).description(e.getDescription())
                .organizer(e.getOrganizer()).coordinator(e.getCoordinator())
                .budget(e.getBudget()).status(e.getStatus())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }

    public Event toEntity(EventDTO dto) {
        return Event.builder()
                .eventName(dto.getEventName()).eventCategory(dto.getEventCategory())
                .festivalDay(dto.getFestivalDay()).festivalYear(dto.getFestivalYear())
                .date(dto.getDate()).startTime(dto.getStartTime()).endTime(dto.getEndTime())
                .venue(dto.getVenue()).description(dto.getDescription())
                .organizer(dto.getOrganizer()).coordinator(dto.getCoordinator())
                .budget(dto.getBudget()).status(dto.getStatus() != null ? dto.getStatus() : "Planned")
                .build();
    }
}
