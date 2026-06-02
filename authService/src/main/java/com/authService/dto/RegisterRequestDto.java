package com.authService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterRequestDto {
    @NotNull(message = "Fullname is required")
    private String fullName;
    @Email
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "Password is required (min 6 characters)")
    private String password;
}
