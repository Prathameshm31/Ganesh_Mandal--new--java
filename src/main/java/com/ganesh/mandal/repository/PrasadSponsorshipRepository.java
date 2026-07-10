package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.PrasadSponsorship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrasadSponsorshipRepository extends JpaRepository<PrasadSponsorship, Long> {

    List<PrasadSponsorship> findByFestivalYearOrderByPrasadDateAscIdAsc(String festivalYear);

    List<PrasadSponsorship> findByFestivalYearAndFestivalDayOrderByPrasadDateAsc(String festivalYear, String festivalDay);

    List<PrasadSponsorship> findBySponsoredByContainingIgnoreCaseOrPrasadNameContainingIgnoreCase(String sponsor, String prasad);
}
