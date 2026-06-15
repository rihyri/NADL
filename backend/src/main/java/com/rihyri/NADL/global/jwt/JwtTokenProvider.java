package com.rihyri.NADL.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // 토큰 생성

    public String createAccessToken(String loginId, String role) {
        return buildToken(loginId, role, accessTokenExpiration);
    }

    public String createRefreshToken(String loginId) {
        return buildToken(loginId, null, refreshTokenExpiration);
    }

    private String buildToken(String loginId, String role, long expiration) {
        JwtBuilder builder = Jwts.builder()
                .subject(loginId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    //  토큰 파싱 / 검증

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("[JWT] 만료된 토큰: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("[JWT] 유효하지 않은 토큰: {}", e.getMessage());
        }
        return false;
    }

    public String getLoginId(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 로그아웃 시 블랙리스트 TTL 계산용
    public long getExpiration(String token) {
        return getClaims(token).getExpiration().getTime() - System.currentTimeMillis();
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    // SecurityContext에 등록할 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String loginId = getLoginId(token);
        String role = getRole(token);

        String authority = (role != null) ? "ROLE_" + role : "ROLE_USER";
        List<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(authority));

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(loginId, "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}