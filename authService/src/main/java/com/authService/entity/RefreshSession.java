package com.authService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_sessions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String refreshToken;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime expiresAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean revoked = false;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String userAgent;

    @ManyToOne
    // because one user can have multiple refresh tokens for different devices/browsers
    private User user;
}

