package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.GaneshMurti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GaneshMurtiRepository extends JpaRepository<GaneshMurti, Long> {

    Optional<GaneshMurti> findFirstByFestivalYearOrderByIdDesc(String festivalYear);

    List<GaneshMurti> findByFestivalYearOrderByIdDesc(String festivalYear);

    List<GaneshMurti> findAllByOrderByFestivalYearDescIdDesc();

    List<GaneshMurti> findByDonatedByContainingIgnoreCase(String donorName);

    List<GaneshMurti> findByFestivalYearContaining(String year);
}
