package com.authService.service.strategy;

import com.authService.dto.LoginRequestDto;
import com.authService.exception.ApiResponse;
import com.authService.service.strategy.type.LoginType;
import org.springframework.stereotype.Component;

@Component
public interface LoginProvider {
    LoginType supportType();

    ApiResponse<?> login(LoginRequestDto loginRequest);
}
