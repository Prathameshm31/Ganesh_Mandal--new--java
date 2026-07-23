package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Member> findByFestivalYear(String festivalYear);

    List<Member> findByCommitteeCategory(String category);

    @Query("SELECT m FROM Member m JOIN m.user u JOIN UserRole ur ON ur.user.id = u.id WHERE ur.role.id = :roleId")
    List<Member> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT m FROM Member m JOIN m.user u JOIN UserRole ur ON ur.user.id = u.id WHERE ur.role.id = :roleId AND m.festivalYear = :year")
    List<Member> findByRoleIdAndFestivalYear(@Param("roleId") Long roleId, @Param("year") String year);

    @Query("SELECT COUNT(m) FROM Member m JOIN m.user u JOIN UserRole ur ON ur.user.id = u.id WHERE ur.role.id = :roleId")
    long countByRoleId(@Param("roleId") Long roleId);
}
