package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    List<UserPermission> findByUserId(Long userId);
    Optional<UserPermission> findByUserIdAndPermissionId(Long userId, Long permissionId);
    void deleteByUserId(Long userId);
    long countByUserId(Long userId);
}
