package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.PermissionDashboardDTO;
import com.ganesh.mandal.dto.UserPermissionDTO;
import com.ganesh.mandal.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-permissions")
@RequiredArgsConstructor
public class UserPermissionController {

    private final UserPermissionService userPermissionService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserPermissionDTO> getUserPermissions(@PathVariable Long userId) {
        return ResponseEntity.ok(userPermissionService.getUserPermissions(userId));
    }

    @PostMapping("/{userId}/assign")
    public ResponseEntity<Void> assignPermission(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        Long permissionId = Long.valueOf(body.get("permissionId").toString());
        boolean isAllowed = Boolean.parseBoolean(body.getOrDefault("isAllowed", "true").toString());
        userPermissionService.assignPermissionToUser(userId, permissionId, isAllowed);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/remove/{permissionId}")
    public ResponseEntity<Void> removePermission(@PathVariable Long userId, @PathVariable Long permissionId) {
        userPermissionService.removeUserPermission(userId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/replace-all")
    public ResponseEntity<Void> replaceAll(@PathVariable Long userId, @RequestBody Map<String, List<Long>> body) {
        userPermissionService.replaceAllUserPermissions(userId, body.get("permissionIds"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/copy-from-role/{roleId}")
    public ResponseEntity<Void> copyFromRole(@PathVariable Long userId, @PathVariable Long roleId) {
        userPermissionService.copyPermissionsFromRole(userId, roleId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{targetUserId}/copy-from-user/{sourceUserId}")
    public ResponseEntity<Void> copyFromUser(@PathVariable Long targetUserId, @PathVariable Long sourceUserId) {
        userPermissionService.copyPermissionsFromUser(targetUserId, sourceUserId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/reset")
    public ResponseEntity<Void> resetToRole(@PathVariable Long userId) {
        userPermissionService.resetToRolePermissions(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<PermissionDashboardDTO> getDashboard() {
        return ResponseEntity.ok(userPermissionService.getDashboardStats());
    }
}
