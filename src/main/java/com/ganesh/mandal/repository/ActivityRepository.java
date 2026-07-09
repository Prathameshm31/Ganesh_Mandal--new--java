package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByStatusOrderByDateDesc(String status);

    List<Activity> findAllByOrderByDateDesc();

    List<Activity> findByCategory(String category);

    List<Activity> findByStatusOrderByDateAsc(String status);
}
