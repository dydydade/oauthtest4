package login.oauthtest4.domain.user.service;

import login.oauthtest4.domain.user.dto.*;
import login.oauthtest4.domain.user.model.Role;
import login.oauthtest4.domain.user.model.User;
import login.oauthtest4.domain.user.repository.UserRepository;
import login.oauthtest4.global.exception.user.RegisteredUserNotFoundException;
import login.oauthtest4.global.exception.user.UnauthorizedAccountAttemptException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    NormalSignUpStrategy normalSignUpStrategy;
    @Mock
    SocialSignUpStrategy socialSignUpStrategy;

    @Test
    @DisplayName("회원 가입 요청이 유효하면 일반 회원 가입이 정상적으로 진행된다.")
    void normalSignUpSuccessTest() {
        // given
        UserNormalSignUpRequest request = UserNormalSignUpRequest.builder()
                .email("test1234@gmail.com")
                .nickname("test_nickname")
                .password("test_password")
                .termsAgreementDto(new UserSignUpTermsAgreementDto())
                .build();

        UserSignUpResponse expectedResponse = new UserSignUpResponse();

        when(normalSignUpStrategy.signUp(request)).thenReturn(expectedResponse);

        // when
        UserSignUpResponse response = userService.signUp(request);

        // then
        assertEquals(expectedResponse, response);
    }

    @Test
    @DisplayName("회원 탈퇴 요청이 유효하면 회원 탈퇴가 정상적으로 진행된다.")
    void signOffSuccessTest() throws MalformedURLException {
        // given
        User user = User.builder()
                .id(1L)
                .email("test1234@gmail.com")
                .nickname("test_nickname")
                .password("test_password")
                .role(Role.USER)
                .imageUrl(new URL("http://example.com"))
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        doNothing().when(userRepository).deleteById(anyLong());

        // when
        UserSignOffResponse userSignOffResponse = userService.signOff(user.getId(), userDetails);

        // then
        assertThat(userSignOffResponse.getUserId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("로그인 사용자와 회원 탈퇴 요청 계정이 일치하지 않으면 예외가 발생한다.")
    void unauthorizedSignOffAttemptTest() throws MalformedURLException {
        // given
        User user = User.builder()
                .id(1L)
                .email("test1234@gmail.com")
                .nickname("test_nickname")
                .password("test_password")
                .role(Role.USER)
                .imageUrl(new URL("http://example.com"))
                .build();

        User unauthorizedUser = User.builder()
                .id(2L)
                .email("unauthorized@gmail.com")
                .nickname("test_nickname")
                .password("test_password")
                .role(Role.USER)
                .imageUrl(new URL("http://example.com"))
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(unauthorizedUser));

        // when
        // then
        assertThatThrownBy(() -> userService.signOff(unauthorizedUser.getId(), userDetails))
                .isInstanceOf(UnauthorizedAccountAttemptException.class);
    }

    @Test
    @DisplayName("존재하지 않는 회원 탈퇴를 요청하면 예외가 발생한다.")
    void nonExistUserSignOffAttemptTest() throws MalformedURLException {
        // given
        Long nonExistentUserId = 123445L;

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test1234@gmail.com")
                .password("test_password")
                .roles("USER")
                .build();

        doThrow(RegisteredUserNotFoundException.class).when(userRepository).findById(nonExistentUserId);

        // when
        // then
        assertThatThrownBy(() -> userService.signOff(nonExistentUserId, userDetails))
                .isInstanceOf(RegisteredUserNotFoundException.class);
    }

    @Test
    @DisplayName("email을 통해 사용자 계정 존재 여부를 조회할 수 있다.")
    void findUserByEmailTest() throws MalformedURLException {
        // given
        User user = User.builder()
                .id(1L)
                .email("test1234@gmail.com")
                .password("test_password")
                .nickname("test_nickname")
                .imageUrl(new URL("http://example.com"))
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));

        // when
        FindUserResponse findUserResponse = userService.findUserByEmail(user.getEmail());

        // then
        assertThat(findUserResponse.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("사용자의 권한에 따라 passwordExists 필드의 값이 다르게 반환된다.")
    void shouldReturnDifferentPasswordExistsBasedOnUserRole() throws MalformedURLException {
        // given
        User user = User.builder()
                .id(1L)
                .email("test1234@gmail.com")
                .password("test_password")
                .nickname("test_nickname")
                .imageUrl(new URL("http://example.com"))
                .role(Role.USER)
                .build();

        User social = User.builder()
                .id(2L)
                .email("social1234@gmail.com")
                .password("test_password")
                .nickname("test_nickname")
                .imageUrl(new URL("http://example.com"))
                .role(Role.SOCIAL)
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findByEmail(social.getEmail())).thenReturn(Optional.ofNullable(social));

        // when
        FindUserResponse findUserResponse = userService.findUserByEmail(user.getEmail());
        FindUserResponse findSocialUserResponse = userService.findUserByEmail(social.getEmail());

        // then
        assertThat(findUserResponse.isPasswordExists()).isEqualTo(true);
        assertThat(findSocialUserResponse.isPasswordExists()).isEqualTo(false);
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 email로 계정을 조회하면 예외가 발생한다.")
    void findUserByEmailFailureTest() throws MalformedURLException {
        // given
        String nonExistentUserEmail = "nonExistentUser@gmail.com";

        doThrow(RegisteredUserNotFoundException.class).when(userRepository).findByEmail(nonExistentUserEmail);

        // when
        // then
        assertThatThrownBy(() -> userService.findUserByEmail(nonExistentUserEmail))
                .isInstanceOf(RegisteredUserNotFoundException.class);
    }

    @Test
    @DisplayName("사용 가능한 닉네임을 조회하면 true가 리턴된다.")
    void checkNickNameAvailabilityTest() {
        // given
        String nickname = "test_nickname";

        when(userRepository.findByNickname(nickname)).thenReturn(Optional.empty());

        // when
        boolean result = userService.checkNicknameAvailability(nickname);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("중복된 닉네임을 조회하면 false 가 리턴된다.")
    void checkDuplicateNickNameAvailabilityTest() {
        // given
        String nickname = "test_nickname";

        when(userRepository.findByNickname(nickname)).thenReturn(Optional.of(User.builder().build()));

        // when
        boolean result = userService.checkNicknameAvailability(nickname);

        // then
        assertThat(result).isFalse();
    }
}