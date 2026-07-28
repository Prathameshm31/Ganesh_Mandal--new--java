package com.ganesh.mandal.repository;

import com.ganesh.mandal.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByTokenAndActiveTrue(String token);

    @Modifying
    @Query("UPDATE UserSession s SET s.active = false, s.loggedOutAt = :now WHERE s.user.id = :userId AND s.active = true")
    void deactivateByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
