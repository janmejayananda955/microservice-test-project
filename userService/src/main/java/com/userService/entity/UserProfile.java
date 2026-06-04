package com.userService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "user_profiles", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId; // Auth Service user id

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "gender")
    private String gender;

    @Column(name = "profile_picture")
    private String profilePicture;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_wishlist", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Column(name = "product_id")
    @Builder.Default
    private List<String> wishlist = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
