package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.PermissionDTO;
import com.ganesh.mandal.entity.Permission;
import com.ganesh.mandal.exception.ResourceNotFoundException;
import com.ganesh.mandal.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAllByOrderByModuleNameAscIdAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PermissionDTO> getPermissionsByModule(String moduleName) {
        return permissionRepository.findByModuleNameOrderById(moduleName).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        return toDTO(permission);
    }

    public PermissionDTO createPermission(PermissionDTO dto) {
        Permission permission = Permission.builder()
                .moduleName(dto.getModuleName())
                .permissionName(dto.getPermissionName())
                .permissionCode(dto.getPermissionCode())
                .description(dto.getDescription())
                .build();
        permission = permissionRepository.save(permission);
        return toDTO(permission);
    }

    public PermissionDTO updatePermission(Long id, PermissionDTO dto) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        if (dto.getModuleName() != null) permission.setModuleName(dto.getModuleName());
        if (dto.getPermissionName() != null) permission.setPermissionName(dto.getPermissionName());
        if (dto.getPermissionCode() != null) permission.setPermissionCode(dto.getPermissionCode());
        if (dto.getDescription() != null) permission.setDescription(dto.getDescription());
        permission = permissionRepository.save(permission);
        return toDTO(permission);
    }

    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
    }

    private PermissionDTO toDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .moduleName(permission.getModuleName())
                .permissionName(permission.getPermissionName())
                .permissionCode(permission.getPermissionCode())
                .description(permission.getDescription())
                .build();
    }
}
