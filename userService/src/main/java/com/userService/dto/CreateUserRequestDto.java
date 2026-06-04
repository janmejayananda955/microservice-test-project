package com.userService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class CreateUserRequestDto {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;
    private String address;
    private String gender;
    private String profilePicture;
}
