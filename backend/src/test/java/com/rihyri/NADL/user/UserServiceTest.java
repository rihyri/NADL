package com.rihyri.NADL.user;

import com.rihyri.NADL.domain.user.dto.login.LoginRequest;
import com.rihyri.NADL.domain.user.dto.login.LoginResponse;
import com.rihyri.NADL.domain.user.dto.login.SignUpRequest;
import com.rihyri.NADL.domain.user.entity.Role;
import com.rihyri.NADL.domain.user.entity.User;
import com.rihyri.NADL.domain.user.repository.UserRepository;
import com.rihyri.NADL.domain.user.service.UserService;
import com.rihyri.NADL.global.exception.CustomException;
import com.rihyri.NADL.global.exception.ErrorCode;
import com.rihyri.NADL.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class
UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UserService userService;

    // 테스트용 공용 데이터
    private SignUpRequest createRequest(String loginId, String email, String nickname) {
        try {
            SignUpRequest request = new SignUpRequest();
            setField(request, "loginId", loginId);
            setField(request, "password", "password123");
            setField(request, "email", email);
            setField(request, "nickname", nickname);
            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object target, String fieldName, String value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // 성공 CASE
    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() {
        SignUpRequest request = createRequest("test123", "test123@gmail.com", "나들이왕");

        // given (준비) : 중복 없음 + 비밀번호 암호화 설정
        given(userRepository.existsByLoginId("test123")).willReturn(false);
        given(userRepository.existsByEmail("test123@gmail.com")).willReturn(false);
        given(userRepository.existsByNickname("나들이왕")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("암호화된비밀번호");

        // when (실행)
        userService.signUp(request);

        // then (검증) : save가 호출됐는지 확인
        verify(userRepository, times(1)).save(any(User.class));
    }

    // 실패 CASE
    @Test
    @DisplayName("아이디 중복 시 DUPLICATE_LOGIN_ID 예외 발생")
    void signUp_fail_duplicatedLoginId() {
        SignUpRequest request = createRequest("test123", "test123@gmail.com", "나들이왕");

        // given : 아이디가 이미 존재
        given(userRepository.existsByLoginId("test123")).willReturn(true);

        // when & then : CustomException이 던져지는지, ErrorCode가 맞는지 검증
        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_LOGIN_ID);
                });

        // save는 호출되지 않아야 함!
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("이메일 중복 시 DUPLICATE_EMAIL 예외 발생")
    void signUp_fail_duplicateEmail() {
        SignUpRequest request = createRequest("test123", "test123@gmail.com", "나들이왕");

        // given : 이메일이 이미 존재
        given(userRepository.existsByLoginId("test123")).willReturn(false);
        given(userRepository.existsByEmail("test123@gmail.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
                });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("닉네임 중복 시 DUPLICATE_NICKNAME 예외 발생")
    void signUp_fail_duplicateNickname() {
        SignUpRequest request = createRequest("test123", "test123@gmail.com", "나들이왕");

        // 닉네임이 이미 존재
        given(userRepository.existsByLoginId("test123")).willReturn(false);
        given(userRepository.existsByEmail("test123@gmail.com")).willReturn(false);
        given(userRepository.existsByNickname("나들이왕")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_NICKNAME);
                });

        verify(userRepository, never()).save(any());
    }

    // 중복 확인 API 테스트
    @Test
    @DisplayName("아이디 중복 확인 - 사용 가능")
    void checkLoginId_available() {
        given(userRepository.existsByLoginId("test123")).willReturn(false);

        // 예외가 안 터지면 성공
        userService.checkLoginId("test123");
    }

    @Test
    @DisplayName("아이디 중복 확인 - 중복")
    void checkLoginId_duplicate() {
        given(userRepository.existsByLoginId("test123")).willReturn(true);

        assertThatThrownBy(() -> userService.checkLoginId("test123"))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_LOGIN_ID);
                });
    }

    @Test
    @DisplayName("이메일 중복 확인 - 사용 가능")
    void checkEmail_available() {
        given(userRepository.existsByEmail("test123@gmail.com")).willReturn(false);

        userService.checkEmail("test123@gmail.com");
    }

    @Test
    @DisplayName("이메일 중복 확인 - 중복")
    void checkEmail_duplicate() {
        given(userRepository.existsByEmail("test123@gmail.com")).willReturn(true);

        assertThatThrownBy(() -> userService.checkEmail("test123@gmail.com"))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
                });
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 사용 가능")
    void checkNickname_available() {
        given(userRepository.existsByNickname("나들이왕")).willReturn(false);

        userService.checkNickname("나들이왕");
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 중복")
    void checkNickname_duplicate() {
        given(userRepository.existsByNickname("나들이왕")).willReturn(true);

        assertThatThrownBy(() -> userService.checkNickname("나들이왕"))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_NICKNAME);
                });
    }

    // 로그인 시작 ─────────────
    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        LoginRequest request = createLoginRequest("test123", "password123");

        User user = User.builder()
                .loginId("test123")
                .password("암호화된비밀번호")
                .email("test@gmail.com")
                .nickname("나들이왕")
                .role(Role.USER)
                .build();

        given(userRepository.findByLoginIdAndIsDeletedFalse("test123"))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "암호화된비밀번호"))
                .willReturn(true);
        given(jwtTokenProvider.createAccessToken("test123", "USER"))
                .willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken("test123"))
                .willReturn("refresh-token");
        given(jwtTokenProvider.getRefreshTokenExpiration())
                .willReturn(604800000L);

        // ← 이 부분 추가
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        LoginResponse response = userService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getNickname()).isEqualTo("나들이왕");

        verify(valueOperations, times(1))
                .set(eq("RT:test123"), eq("refresh-token"), any(Duration.class));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 아이디")
    void login_fail_userNotFound() {
        LoginRequest request = createLoginRequest("test123", "password123");
        given(userRepository.findByLoginIdAndIsDeletedFalse("test123"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
                });
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrongPassword() {
        LoginRequest request = createLoginRequest("test123", "wrongPassword");

        User user = User.builder()
                .loginId("test123")
                .password("암호화된비밀번호")
                .email("test123@gmail.com")
                .nickname("나들이왕")
                .role(Role.USER)
                .build();

        given(userRepository.findByLoginIdAndIsDeletedFalse("test123")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", "암호화된비밀번호")).willReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> {
                    CustomException ce = (CustomException) e;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
                });

        verify(jwtTokenProvider, never()).createAccessToken(any(), any());
    }

    private LoginRequest createLoginRequest(String loginId, String password) {
        try {
            LoginRequest request = new LoginRequest();
            setField(request, "loginId", loginId);
            setField(request, "password", password);
            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
