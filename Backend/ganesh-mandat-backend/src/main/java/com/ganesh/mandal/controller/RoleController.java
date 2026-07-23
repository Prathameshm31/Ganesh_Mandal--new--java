package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.MemberDTO;
import com.ganesh.mandal.dto.RoleDTO;
import com.ganesh.mandal.dto.UserDTO;
import com.ganesh.mandal.service.MemberService;
import com.ganesh.mandal.service.RoleService;
import com.ganesh.mandal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final MemberService memberService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<MemberDTO>> getUsersByRole(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMembersByRole(id));
    }

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO dto) {
        return ResponseEntity.ok(roleService.createRole(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody RoleDTO dto) {
        return ResponseEntity.ok(roleService.updateRole(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{targetRoleId}/copy-from/{sourceRoleId}")
    public ResponseEntity<Void> copyPermissions(@PathVariable Long targetRoleId, @PathVariable Long sourceRoleId) {
        roleService.copyPermissionsFromRole(targetRoleId, sourceRoleId);
        return ResponseEntity.ok().build();
    }
}
