package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.Colony;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColonyRepository extends JpaRepository<Colony, Long> {

    List<Colony> findByNameContainingIgnoreCase(String name);
}
