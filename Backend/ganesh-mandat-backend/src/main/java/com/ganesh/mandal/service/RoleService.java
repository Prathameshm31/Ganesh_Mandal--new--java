package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.RoleDTO;
import com.ganesh.mandal.entity.Permission;
import com.ganesh.mandal.entity.Role;
import com.ganesh.mandal.entity.RolePermission;
import com.ganesh.mandal.entity.UserRole;
import com.ganesh.mandal.exception.ResourceNotFoundException;
import com.ganesh.mandal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return toDTO(role);
    }

    public RoleDTO createRole(RoleDTO dto) {
        Role role = Role.builder().roleName(dto.getRoleName()).description(dto.getDescription()).status("ACTIVE").build();
        role = roleRepository.save(role);
        if (dto.getPermissionIds() != null) assignPermissionsToRole(role, dto.getPermissionIds());
        return toDTO(role);
    }

    public RoleDTO updateRole(Long id, RoleDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        if (dto.getRoleName() != null) role.setRoleName(dto.getRoleName());
        if (dto.getDescription() != null) role.setDescription(dto.getDescription());
        if (dto.getStatus() != null) role.setStatus(dto.getStatus());
        role = roleRepository.save(role);
        if (dto.getPermissionIds() != null) {
            rolePermissionRepository.deleteByRoleId(role.getId());
            assignPermissionsToRole(role, dto.getPermissionIds());
        }
        return toDTO(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) throw new ResourceNotFoundException("Role not found with id: " + id);
        rolePermissionRepository.deleteByRoleId(id);
        roleRepository.deleteById(id);
    }

    @Transactional
    public void copyPermissionsFromRole(Long targetRoleId, Long sourceRoleId) {
        rolePermissionRepository.deleteByRoleId(targetRoleId);
        Role targetRole = roleRepository.findById(targetRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        List<RolePermission> sourcePerms = rolePermissionRepository.findByRoleId(sourceRoleId);
        for (RolePermission rp : sourcePerms) {
            rolePermissionRepository.save(RolePermission.builder().role(targetRole).permission(rp.getPermission()).build());
        }
    }

    private void assignPermissionsToRole(Role role, List<Long> permissionIds) {
        for (Long permId : permissionIds) {
            Permission permission = permissionRepository.findById(permId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permId));
            rolePermissionRepository.save(RolePermission.builder().role(role).permission(permission).build());
        }
    }

    private RoleDTO toDTO(Role role) {
        List<Long> permissionIds = rolePermissionRepository.findByRoleId(role.getId())
                .stream().map(rp -> rp.getPermission().getId()).collect(Collectors.toList());
        List<String> permissionCodes = rolePermissionRepository.findByRoleId(role.getId())
                .stream().map(rp -> rp.getPermission().getPermissionCode()).collect(Collectors.toList());
        long userCount = userRoleRepository.countByRoleId(role.getId());
        return RoleDTO.builder()
                .id(role.getId()).roleName(role.getRoleName())
                .description(role.getDescription()).status(role.getStatus())
                .permissionIds(permissionIds).permissionCodes(permissionCodes)
                .userCount(userCount).build();
    }
}
