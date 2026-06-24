package com.rihyri.NADL.domain.user.service;

import com.rihyri.NADL.domain.user.dto.LoginRequest;
import com.rihyri.NADL.domain.user.dto.LoginResponse;
import com.rihyri.NADL.domain.user.dto.SignUpRequest;
import com.rihyri.NADL.domain.user.entity.Role;
import com.rihyri.NADL.domain.user.entity.User;
import com.rihyri.NADL.domain.user.repository.UserRepository;
import com.rihyri.NADL.global.exception.CustomException;
import com.rihyri.NADL.global.exception.ErrorCode;
import com.rihyri.NADL.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    // 아이디 중복 확인
    public void checkLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    // 이메일 중복 확인
    public void checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    // 닉네임 중복 확인
    public void checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    // 회원가입 (Duplication 검사 한번 더)
    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        userRepository.save(User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .nickname(request.getNickname())
                .role(Role.USER)
                .build());
    }

    // 로그인
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // 1. 유저 조회 (탈퇴한 유저 제외)
        User user = userRepository.findByLoginIdAndIsDeletedFalse(request.getLoginId())
                .orElseThrow(() -> {
                    log.warn("[로그인 실패] 존재하지 않는 아이디: {}", request.getLoginId());
                    return new CustomException(ErrorCode.INVALID_CREDENTIALS);
                });

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("[로그인 실패] 비밀번호 불일치 - loginId: {}", request.getLoginId());
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 3. 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getLoginId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId());

        // 4. Refresh Token Redis 저장 (key: RT:{loginId})
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getLoginId(),
                refreshToken,
                Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpiration())
        );

        return new LoginResponse(accessToken, refreshToken, user.getNickname());
    }
}
