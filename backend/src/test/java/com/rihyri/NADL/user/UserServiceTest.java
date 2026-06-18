package com.rihyri.NADL.user;

import com.rihyri.NADL.domain.user.dto.SignUpRequest;
import com.rihyri.NADL.domain.user.entity.User;
import com.rihyri.NADL.domain.user.repository.UserRepository;
import com.rihyri.NADL.domain.user.service.UserService;
import com.rihyri.NADL.global.exception.CustomException;
import com.rihyri.NADL.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
