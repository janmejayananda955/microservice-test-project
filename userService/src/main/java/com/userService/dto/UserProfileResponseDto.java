package com.userService.dto;

import com.userService.entity.UserProfile;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {

    private UUID id;
    private UUID userId;
    private String fullName;
    private String phone;
    private String address;
    private String gender;
    private String profilePicture;
    private List<String> wishlist;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserProfileResponseDto fromEntity(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        return UserProfileResponseDto.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .phone(profile.getPhone())
                .address(profile.getAddress())
                .gender(profile.getGender())
                .profilePicture(profile.getProfilePicture())
                .wishlist(profile.getWishlist())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
