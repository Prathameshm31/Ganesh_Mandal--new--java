package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByNameContainingIgnoreCaseOrMobileContaining(String name, String mobile);

    long countByColony(String colony);

    List<Member> findByColony(String colony);

    long countByStatus(String status);

    List<Member> findByStatus(String status);

    List<Member> findByOccupation(String occupation);

    List<Member> findFirst5ByOrderByCreatedAtDesc();
}
