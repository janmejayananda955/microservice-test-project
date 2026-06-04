package com.authService.dto;


import com.authService.service.strategy.type.LoginType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequestDto {

    @NotNull(message = "Login type is required")
    private LoginType type;
    @Email
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "Password is required (min 6 characters)")
    private String password;

    private HttpServletRequest request;
    private HttpServletResponse response;
}