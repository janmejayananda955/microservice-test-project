package com.authService.repository;

import com.authService.entity.RefreshSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {
}