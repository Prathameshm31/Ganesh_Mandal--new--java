package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionCode(String permissionCode);
    List<Permission> findByModuleNameOrderById(String moduleName);
    List<Permission> findAllByOrderByModuleNameAscIdAsc();
}
