package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleId(Long roleId);
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from RolePermission rp where rp.role.id = :roleId")
    void deleteByRoleId(Long roleId);
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
