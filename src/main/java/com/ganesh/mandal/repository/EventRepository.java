package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByFestivalYear(String festivalYear);
    List<Event> findByEventCategory(String eventCategory);
    List<Event> findByFestivalDay(String festivalDay);
    List<Event> findByDate(LocalDate date);
    List<Event> findByFestivalYearAndEventCategory(String festivalYear, String eventCategory);
    List<Event> findByFestivalYearAndStatus(String festivalYear, String status);
}
