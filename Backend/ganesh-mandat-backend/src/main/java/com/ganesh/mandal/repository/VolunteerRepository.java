package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    List<Volunteer> findByFestivalYear(String festivalYear);
    List<Volunteer> findByCategory(String category);
    List<Volunteer> findByCategoryAndFestivalYear(String category, String festivalYear);
    List<Volunteer> findByRole(String role);
    List<Volunteer> findByStatus(String status);
    List<Volunteer> findByStatusAndFestivalYear(String status, String festivalYear);

    @Query("SELECT v FROM Volunteer v WHERE v.role IN :roles")
    List<Volunteer> findByRoles(@Param("roles") List<String> roles);

    @Query("SELECT v FROM Volunteer v WHERE v.festivalYear = :year AND v.role IN :roles")
    List<Volunteer> findByFestivalYearAndRoles(@Param("year") String festivalYear, @Param("roles") List<String> roles);

    @Query("SELECT v FROM Volunteer v WHERE MONTH(v.dateOfBirth) = :month")
    List<Volunteer> findByDateOfBirthMonth(@Param("month") int month);

    @Query("SELECT v FROM Volunteer v WHERE v.festivalYear = :year AND MONTH(v.dateOfBirth) = :month")
    List<Volunteer> findByFestivalYearAndDateOfBirthMonth(@Param("year") String festivalYear, @Param("month") int month);
}
