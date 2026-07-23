package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrMobileContaining(String name, String email, String mobile);
    long countByStatus(String status);
}
