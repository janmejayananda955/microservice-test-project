package com.userService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateProfileRequestDto {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;
    private String address;
    private String gender;
    private String profilePicture;
}
