package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);
    void deleteByUserId(Long userId);
    List<UserRole> findByRoleId(Long roleId);
    long countByRoleId(Long roleId);
}
