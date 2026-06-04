package com.userService.repository;

import com.userService.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    boolean existsByUserId(UUID userId);
    Optional<UserProfile> findByUserId(UUID userId);
}