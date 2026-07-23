package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.UserDTO;
import com.ganesh.mandal.entity.User;
import com.ganesh.mandal.exception.ResourceNotFoundException;
import com.ganesh.mandal.repository.UserRepository;
import com.ganesh.mandal.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toDTO(user);
    }

    public List<UserDTO> searchUsers(String keyword) {
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrMobileContaining(keyword, keyword, keyword)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword() != null ? dto.getPassword() : "changeme")
                .name(dto.getName())
                .email(dto.getEmail())
                .mobile(dto.getMobile())
                .status("ACTIVE")
                .build();
        user = userRepository.save(user);
        return toDTO(user);
    }

    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getMobile() != null) user.setMobile(dto.getMobile());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        user = userRepository.save(user);
        return toDTO(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDTO toDTO(User user) {
        List<String> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList());
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .status(user.getStatus())
                .role(String.join(", ", roles))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
