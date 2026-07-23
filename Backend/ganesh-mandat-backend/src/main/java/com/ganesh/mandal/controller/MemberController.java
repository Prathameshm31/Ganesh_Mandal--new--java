package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.MemberDTO;
import com.ganesh.mandal.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@Valid @RequestBody MemberDTO dto) {
        MemberDTO created = memberService.createMember(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @Valid @RequestBody MemberDTO dto) {
        return ResponseEntity.ok(memberService.updateMember(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MemberDTO>> searchMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String colony,
            @RequestParam(required = false) String occupation,
            @RequestParam(required = false) String festivalYear,
            @RequestParam(required = false) String committeeCategory,
            @RequestParam(required = false) Long roleId) {
        return ResponseEntity.ok(memberService.filterMembers(keyword, status, colony, occupation, festivalYear, committeeCategory, roleId));
    }

    @GetMapping("/by-role/{roleId}")
    public ResponseEntity<List<MemberDTO>> getMembersByRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(memberService.getMembersByRole(roleId));
    }

    @PutMapping("/{id}/assign-role/{roleId}")
    public ResponseEntity<Void> assignRole(@PathVariable Long id, @PathVariable Long roleId) {
        MemberDTO dto = memberService.getMemberById(id);
        dto.setRoleId(roleId);
        memberService.updateMember(id, dto);
        return ResponseEntity.ok().build();
    }
}
