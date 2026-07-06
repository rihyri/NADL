package com.rihyri.NADL.domain.user.controller;

import com.rihyri.NADL.domain.user.dto.login.LoginRequest;
import com.rihyri.NADL.domain.user.dto.login.LoginResponse;
import com.rihyri.NADL.domain.user.dto.login.SignUpRequest;
import com.rihyri.NADL.domain.user.dto.login.TokenRefreshRequest;
import com.rihyri.NADL.domain.user.service.UserService;
import com.rihyri.NADL.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 아이디 중복 확인
    @GetMapping("/check/login-id")
    public ResponseEntity<ApiResponse<Void>> checkLoginId(@RequestParam String value) {
        userService.checkLoginId(value);
        return ResponseEntity.ok(ApiResponse.ok("사용 가능한 아이디입니다."));
    }

    // 이메일 중복 확인
    @GetMapping("/check/email")
    public ResponseEntity<ApiResponse<Void>> checkEmail(@RequestParam String value) {
        userService.checkEmail(value);
        return ResponseEntity.ok(ApiResponse.ok("사용 가능한 이메일입니다."));
    }

    // 닉네임 중복 확인
    @GetMapping("/check/nickname")
    public ResponseEntity<ApiResponse<Void>> checkNickname(@RequestParam String value) {
        userService.checkNickname(value);
        return ResponseEntity.ok(ApiResponse.ok("사용 가능한 닉네임입니다."));
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
        userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("회원가입이 완료되었습니다."));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("로그인에 성공했습니다.", userService.login(request)));
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("토큰이 재발급되었습니다.", userService.reissue(request)));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String bearerToken,
            @AuthenticationPrincipal UserDetails userDetails) {

        String accessToken = bearerToken.substring(7); // "Bearer " 제거
        userService.logout(accessToken, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 되었습니다."));
    }
}
