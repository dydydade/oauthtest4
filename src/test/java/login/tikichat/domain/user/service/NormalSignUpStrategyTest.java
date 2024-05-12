package login.tikichat.domain.user.service;

import login.tikichat.domain.terms.model.AgreementHistory;
import login.tikichat.domain.terms.service.TermsService;
import login.tikichat.domain.user.dto.UserSignUpRequest;
import login.tikichat.domain.user.dto.UserSignUpResponse;
import login.tikichat.domain.user.dto.UserSignUpTermsAgreementDto;
import login.tikichat.domain.user.model.Role;
import login.tikichat.domain.user.model.SocialProfile;
import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.exception.terms.RequiredTermsNotAgreedException;
import login.tikichat.global.exception.user.AlreadySignedUpUserException;
import login.tikichat.global.exception.user.NicknameAlreadyInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NormalSignUpStrategyTest {

    @InjectMocks
    NormalSignUpStrategy signUpStrategy;
    @Mock
    CommonSignUpServices services;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @Mock
    TermsService termsService;

    User user;

    UserSignUpTermsAgreementDto termsAgreementDto;

    UserSignUpRequest userSignUpRequest;

    UserSignUpResponse userSignUpResponse;

    @BeforeEach
    void setUp() throws MalformedURLException {
        Long userId = 1L;
        String email = "test1234@example.com";
        String nickname = "test1234";
        String password = "encodedPassword";
        URL imageUrl = new URL("http://example.com");
        Role role = Role.USER;

        termsAgreementDto = UserSignUpTermsAgreementDto.builder()
                .agreements(List.of(new UserSignUpTermsAgreementDto.TermsAgreement(1L, true, "10.226.234.12", "용찬 의 iPhone 14")))
                .build();

        userSignUpRequest = UserSignUpRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .termsAgreementDto(termsAgreementDto)
                .build();

        userSignUpResponse = UserSignUpResponse.builder()
                .userId(userId)
                .email(email)
                .nickname(nickname)
                .build();

        user = User.builder()
                .id(userId)
                .email(email)
                .nickname(nickname)
                .password(password)
                .role(role)
                .imageUrl(imageUrl)
                .socialProfiles(List.of(SocialProfile.builder().build()))
                .termsAgreementHistories(List.of(AgreementHistory.builder().build()))
                .build();
    }

    @Test
    @DisplayName("회원가입 요청이 유효하면 회원가입이 되고 응답이 반환된다.")
    void testSignUpWithValidData() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByNickname(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(services.toSignUpResponse(user)).thenReturn(userSignUpResponse);

        // when
        UserSignUpResponse response = signUpStrategy.signUp(userSignUpRequest);

        // then
        assertThat(response).isEqualTo(userSignUpResponse);
    }

    @Test
    @DisplayName("기존 회원과 중복된 이메일로 회원가입을 시도하면 예외가 발생한다.")
    void duplicateEmailSignUpRequest() {
        // given
        when(userRepository.findByEmail(userSignUpRequest.getEmail())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> signUpStrategy.signUp(userSignUpRequest))
                .isInstanceOf(AlreadySignedUpUserException.class);
    }

    @Test
    @DisplayName("기존 회원과 중복된 닉네임으로 회원가입을 시도하면 예외가 발생한다.")
    void duplicateNicknameSignUpRequest() {
        // given
        when(userRepository.findByNickname(userSignUpRequest.getNickname())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> signUpStrategy.signUp(userSignUpRequest))
                .isInstanceOf(NicknameAlreadyInUseException.class);
    }

    @Test
    @DisplayName("필수 약관에 모두 동의하지 않고 회원가입을 시도하면 예외가 발생한다.")
    void doNotConsentAllRequiredTermsSignUpRequest() {
        // given
        doThrow(RequiredTermsNotAgreedException.class).when(termsService).validateRequiredTermsConsents(termsAgreementDto);

        // when
        // then
        assertThatThrownBy(() -> signUpStrategy.signUp(userSignUpRequest))
                .isInstanceOf(RequiredTermsNotAgreedException.class);
    }
}