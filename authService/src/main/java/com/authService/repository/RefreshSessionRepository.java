package com.authService.repository;

import com.authService.entity.RefreshSession;
import com.authService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {

    @Query("SELECT rs FROM RefreshSession rs" +
            " WHERE rs.refreshToken = :refreshToken ")
    //AND rs.revoked = false AND rs.expiresAt > CURRENT_TIMESTAMP
    Optional<RefreshSession> findByRefreshToken(String refreshToken);

    @Query("DELETE FROM RefreshSession r WHERE r.user.id = :id")
    @Modifying
    void deleteByUser(User user);

    @Query("UPDATE RefreshSession r SET r.revoked = true WHERE r.user.id = :id AND r.revoked = false")
    @Modifying
    void revokeAllByUserId(UUID id);

    @Query("SELECT COUNT(r) FROM RefreshSession r WHERE r.user = :user AND r.revoked = false")
    int countActiveByUser(User user);
}