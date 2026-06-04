package com.authService.service.strategy;

import com.authService.dto.LoginRequestDto;
import com.authService.entity.RefreshSession;
import com.authService.entity.User;
import com.authService.exception.ApiResponse;
import com.authService.exception.InvalidCredentialsException;
import com.authService.exception.ResourceNotFoundException;
import com.authService.repository.RefreshSessionRepository;
import com.authService.repository.UserRepository;
import com.authService.security.AuthUtil;
import com.authService.security.CustomUserDetails;
import com.authService.service.strategy.type.LoginType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailPasswordLoginProvider implements LoginProvider {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final RefreshSessionRepository refreshSessionRepository;

    @Override
    public LoginType supportType() {
        return LoginType.EMAIL_PASSWORD;
    }

    @Override
    @Transactional
    public ApiResponse<?> login(LoginRequestDto loginRequestDto) {
        if (loginRequestDto == null) {
            throw new ResourceNotFoundException("Login request cannot be null");
        }

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        if (refreshSessionRepository.countActiveByUser(user) >= 3) {
            throw new ResourceNotFoundException("User exceeded the device login limit");
        }

        String accessToken = authUtil.generateAccessToken(customUserDetails);
        String refreshToken = authUtil.generateRefreshToken(customUserDetails);

        saveRefreshToken(user, refreshToken, loginRequestDto.getRequest());
        authUtil.setRefreshTokenInCookie(refreshToken, loginRequestDto.getResponse());

        return ApiResponse.success(HttpStatus.OK.value(), "Login successful", accessToken);
    }

    private void saveRefreshToken(User user, String refreshToken, HttpServletRequest request) {
        refreshSessionRepository.save(RefreshSession.builder()
                .refreshToken(DigestUtils.sha256Hex(refreshToken))
                .user(user)
                .expiresAt(java.time.LocalDateTime.now().plusDays(7))
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .build()
        );
    }
}
