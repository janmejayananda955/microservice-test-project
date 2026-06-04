package com.authService.dto;

import com.authService.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterRequestDto {

    @Email
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "Password is required (min 6 characters)")
    private String password;

    // restrict User to register as Admin
    @NotNull(message = "Role is required (CUSTOMER, RESTAURANT_OWNER, DELIVERY_PARTNER)")
    private Role role;
}
