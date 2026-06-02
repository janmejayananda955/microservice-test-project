package com.authService.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class AuthUtil {

    @Value("${jwt.access.secret}")
    private String JWT_ACCESS_SECRET;

    @Value("${jwt.refresh.secret}")
    private String JWT_REFRESH_SECRET;

    private SecretKey accessSecretKey() {
        return Keys.hmacShaKeyFor(JWT_ACCESS_SECRET.getBytes(StandardCharsets.UTF_8));
    }
    private SecretKey refreshSecretKey() {
        return Keys.hmacShaKeyFor(JWT_REFRESH_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(CustomUserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("id", userDetails.getId())
                .signWith(accessSecretKey())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(accessSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String generateRefreshToken(CustomUserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("id", userDetails.getId())
                .signWith(refreshSecretKey())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .compact();
    }

    public void setRefreshTokenInCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
