package com.authService.service.impl;

import com.authService.dto.LoginRequestDto;
import com.authService.exception.ApiResponse;
import com.authService.exception.ResourceNotFoundException;
import com.authService.service.AuthenticationService;
import com.authService.service.strategy.LoginProvider;
import com.authService.service.strategy.type.LoginType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final Map<LoginType, LoginProvider> providerMap;

    public AuthenticationServiceImpl(List<LoginProvider> providers) {
        this.providerMap = providers.stream().collect(Collectors
                .toMap(LoginProvider::supportType, Function.identity()));
    }

    @Override
    public ApiResponse<?> login(LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        if (loginRequestDto == null) {
            throw new ResourceNotFoundException("Login request cannot be null");
        }
        LoginProvider provider = providerMap.get(loginRequestDto.getType());
        if (provider == null) {
            throw new ResourceNotFoundException("Unsupported login type: " + loginRequestDto.getType());
        }
        loginRequestDto.setRequest(request);
        loginRequestDto.setResponse(response);
        return provider.login(loginRequestDto);
    }
}


