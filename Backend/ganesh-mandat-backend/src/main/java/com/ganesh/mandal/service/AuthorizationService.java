package com.ganesh.mandal.service;

import com.ganesh.mandal.entity.*;
import com.ganesh.mandal.exception.AccessDeniedException;
import com.ganesh.mandal.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionRepository userPermissionRepository;

    public Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing or invalid authorization header");
        }
        try {
            String token = authHeader.substring(7);
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":", 2);
            String username = parts[0];
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AccessDeniedException("User not found"));
            return user.getId();
        } catch (Exception e) {
            throw new AccessDeniedException("Invalid authorization token");
        }
    }

    public void requirePermission(HttpServletRequest request, String permissionCode) {
        Long userId = getCurrentUserId(request);
        if (!hasPermission(userId, permissionCode)) {
            throw new AccessDeniedException("Access denied: " + permissionCode + " permission required");
        }
    }

    public boolean hasPermission(Long userId, String permissionCode) {
        Permission permission = permissionRepository.findByPermissionCode(permissionCode).orElse(null);
        if (permission == null) return false;

        Set<Long> grantedPermIds = new HashSet<>();
        Set<Long> deniedPermIds = new HashSet<>();

        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        for (UserRole ur : userRoles) {
            List<RolePermission> rps = rolePermissionRepository.findByRoleId(ur.getRole().getId());
            for (RolePermission rp : rps) {
                grantedPermIds.add(rp.getPermission().getId());
            }
        }

        List<UserPermission> userPerms = userPermissionRepository.findByUserId(userId);
        for (UserPermission up : userPerms) {
            if (Boolean.TRUE.equals(up.getIsAllowed())) {
                grantedPermIds.add(up.getPermission().getId());
            } else {
                deniedPermIds.add(up.getPermission().getId());
            }
        }

        if (deniedPermIds.contains(permission.getId())) return false;
        return grantedPermIds.contains(permission.getId());
    }

    public List<String> getUserPermissions(Long userId) {
        Set<String> codes = new HashSet<>();

        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        for (UserRole ur : userRoles) {
            List<RolePermission> rps = rolePermissionRepository.findByRoleId(ur.getRole().getId());
            for (RolePermission rp : rps) {
                codes.add(rp.getPermission().getPermissionCode());
            }
        }

        List<UserPermission> userPerms = userPermissionRepository.findByUserId(userId);
        for (UserPermission up : userPerms) {
            if (Boolean.TRUE.equals(up.getIsAllowed())) {
                codes.add(up.getPermission().getPermissionCode());
            } else {
                codes.remove(up.getPermission().getPermissionCode());
            }
        }

        return new ArrayList<>(codes);
    }
}
