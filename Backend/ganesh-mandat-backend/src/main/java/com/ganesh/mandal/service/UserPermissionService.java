package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.PermissionDashboardDTO;
import com.ganesh.mandal.dto.UserPermissionDTO;
import com.ganesh.mandal.entity.*;
import com.ganesh.mandal.exception.ResourceNotFoundException;
import com.ganesh.mandal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPermissionService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public UserPermissionDTO getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Permission> allPermissions = permissionRepository.findAllByOrderByModuleNameAscIdAsc();
        Set<Long> rolePermissionIds = getRolePermissionIds(userId);
        Map<Long, Boolean> userPermissionMap = getUserPermissionMap(userId);

        List<UserPermissionDTO.ModulePermissions> modulePermissions = new ArrayList<>();
        Map<String, List<Permission>> grouped = allPermissions.stream()
                .collect(Collectors.groupingBy(Permission::getModuleName, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<String, List<Permission>> entry : grouped.entrySet()) {
            List<UserPermissionDTO.PermissionEntry> entries = entry.getValue().stream()
                    .map(p -> {
                        boolean fromRole = rolePermissionIds.contains(p.getId());
                        boolean allowed;
                        if (userPermissionMap.containsKey(p.getId())) {
                            allowed = userPermissionMap.get(p.getId());
                        } else {
                            allowed = fromRole;
                        }
                        return UserPermissionDTO.PermissionEntry.builder()
                                .permissionId(p.getId())
                                .permissionName(p.getPermissionName())
                                .permissionCode(p.getPermissionCode())
                                .allowed(allowed)
                                .fromRole(fromRole && !userPermissionMap.containsKey(p.getId()))
                                .build();
                    })
                    .collect(Collectors.toList());

            modulePermissions.add(UserPermissionDTO.ModulePermissions.builder()
                    .moduleName(entry.getKey())
                    .permissions(entries)
                    .build());
        }

        return UserPermissionDTO.builder()
                .userId(user.getId())
                .userName(user.getName())
                .userRole(getUserRoleNames(userId))
                .modulePermissions(modulePermissions)
                .build();
    }

    @Transactional
    public void assignPermissionToUser(Long userId, Long permissionId, boolean isAllowed) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        UserPermission up = userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId)
                .orElse(UserPermission.builder().user(user).permission(permission).build());
        up.setIsAllowed(isAllowed);
        userPermissionRepository.save(up);
    }

    @Transactional
    public void removeUserPermission(Long userId, Long permissionId) {
        userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId)
                .ifPresent(userPermissionRepository::delete);
    }

    @Transactional
    public void replaceAllUserPermissions(Long userId, List<Long> allowedPermissionIds) {
        userPermissionRepository.deleteByUserId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<Long> rolePermIds = getRolePermissionIds(userId);

        for (Long permId : allowedPermissionIds) {
            if (!rolePermIds.contains(permId)) {
                Permission permission = permissionRepository.findById(permId)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
                UserPermission up = UserPermission.builder()
                        .user(user).permission(permission).isAllowed(true)
                        .build();
                userPermissionRepository.save(up);
            }
        }

        for (Long permId : rolePermIds) {
            if (!allowedPermissionIds.contains(permId)) {
                permissionRepository.findById(permId).ifPresent(p -> {
                    UserPermission up = UserPermission.builder()
                            .user(user).permission(p).isAllowed(false)
                            .build();
                    userPermissionRepository.save(up);
                });
            }
        }
    }

    @Transactional
    public void copyPermissionsFromRole(Long userId, Long roleId) {
        userPermissionRepository.deleteByUserId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<RolePermission> rolePerms = rolePermissionRepository.findByRoleId(roleId);
        for (RolePermission rp : rolePerms) {
            UserPermission up = UserPermission.builder()
                    .user(user).permission(rp.getPermission()).isAllowed(true)
                    .build();
            userPermissionRepository.save(up);
        }
    }

    @Transactional
    public void copyPermissionsFromUser(Long targetUserId, Long sourceUserId) {
        userPermissionRepository.deleteByUserId(targetUserId);
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));
        List<UserPermission> sourcePerms = userPermissionRepository.findByUserId(sourceUserId);
        for (UserPermission sp : sourcePerms) {
            UserPermission up = UserPermission.builder()
                    .user(targetUser).permission(sp.getPermission())
                    .isAllowed(sp.getIsAllowed()).build();
            userPermissionRepository.save(up);
        }
    }

    @Transactional
    public void resetToRolePermissions(Long userId) {
        userPermissionRepository.deleteByUserId(userId);
    }

    public PermissionDashboardDTO getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalRoles = roleRepository.count();
        long totalPermissions = permissionRepository.count();
        long activeUsers = userRepository.countByStatus("ACTIVE");
        long usersWithCustomPerms = 0;
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (userPermissionRepository.countByUserId(u.getId()) > 0) {
                usersWithCustomPerms++;
            }
        }

        return PermissionDashboardDTO.builder()
                .totalUsers(totalUsers)
                .totalRoles(totalRoles)
                .totalPermissions(totalPermissions)
                .activeUsers(activeUsers)
                .usersWithCustomPermissions(usersWithCustomPerms)
                .recentlyUpdated(List.of("System initialized"))
                .build();
    }

    private Set<Long> getRolePermissionIds(Long userId) {
        Set<Long> ids = new HashSet<>();
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        for (UserRole ur : userRoles) {
            List<RolePermission> rps = rolePermissionRepository.findByRoleId(ur.getRole().getId());
            for (RolePermission rp : rps) {
                ids.add(rp.getPermission().getId());
            }
        }
        return ids;
    }

    private Map<Long, Boolean> getUserPermissionMap(Long userId) {
        Map<Long, Boolean> map = new HashMap<>();
        List<UserPermission> ups = userPermissionRepository.findByUserId(userId);
        for (UserPermission up : ups) {
            map.put(up.getPermission().getId(), up.getIsAllowed());
        }
        return map;
    }

    private String getUserRoleNames(Long userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.joining(", "));
    }
}
