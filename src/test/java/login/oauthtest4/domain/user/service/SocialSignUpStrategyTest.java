package login.oauthtest4.domain.user.service;

import login.oauthtest4.domain.terms.model.AgreementHistory;
import login.oauthtest4.domain.terms.service.TermsService;
import login.oauthtest4.domain.user.dto.*;
import login.oauthtest4.domain.user.model.Role;
import login.oauthtest4.domain.user.model.SocialProfile;
import login.oauthtest4.domain.user.model.SocialType;
import login.oauthtest4.domain.user.model.User;
import login.oauthtest4.domain.user.repository.SocialProfileRepository;
import login.oauthtest4.domain.user.repository.UserRepository;
import login.oauthtest4.global.exception.terms.RequiredTermsNotAgreedException;
import login.oauthtest4.global.exception.user.AlreadySignedUpUserException;
import login.oauthtest4.global.exception.user.NicknameAlreadyInUseException;
import login.oauthtest4.global.exception.user.SocialEmailMismatchException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SocialSignUpStrategyTest {

    @InjectMocks
    SocialSignUpStrategy signUpStrategy;
    @Mock
    CommonSignUpServices services;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @Mock
    SocialProfileRepository socialProfileRepository;
    @Mock
    TermsService termsService;

    User user;

    UserSignUpTermsAgreementDto termsAgreementDto;

    UserSignUpSocialProfileDto userSignUpSocialProfileDto;

    UserSocialSignUpRequest userSocialSignUpRequest;

    UserSignUpResponse userSignUpResponse;

    @BeforeEach
    void setUp() throws MalformedURLException {
        Long userId = 1L;
        String email = "test1234@example.com";
        String nickname = "test1234";
        String password = "encodedPassword";
        URL imageUrl = new URL("http://example.com");
        Role role = Role.USER;

        String socialId = "123123242";
        String socialEmail = "test1234@example.com";
        SocialType socialType = SocialType.GOOGLE;

        termsAgreementDto = UserSignUpTermsAgreementDto.builder()
                .agreements(List.of(new UserSignUpTermsAgreementDto.TermsAgreement(1L, true, "10.226.234.12", "용찬 의 iPhone 14")))
                .build();

        userSignUpSocialProfileDto = UserSignUpSocialProfileDto.builder()
                .socialId(socialId)
                .socialEmail(socialEmail)
                .socialType(socialType)
                .build();

        userSocialSignUpRequest = UserSocialSignUpRequest.builder()
                .socialProfileDto(userSignUpSocialProfileDto)
                .email(email)
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
    @DisplayName("회원가입 요청이 유효하면 회원가입이 되고 응답이 반환된다. 소셜 회원가입은 소셜 연동 정보를 포함해야 함.")
    void testSignUpWithValidData() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByNickname(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(socialProfileRepository.save(any(SocialProfile.class))).thenReturn(SocialProfile.builder().build());
        when(services.toSignUpResponse(user)).thenReturn(userSignUpResponse);

        // when
        UserSignUpResponse response = signUpStrategy.signUp(userSocialSignUpRequest);

        // then
        assertThat(response).isEqualTo(userSignUpResponse);
    }

    @Test
    @DisplayName("기존 회원과 중복된 이메일로 회원가입을 시도하면 예외가 발생한다.")
    void duplicateEmailSignUpRequest() {
        // given
        when(userRepository.findByEmail(userSocialSignUpRequest.getEmail())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> signUpStrategy.signUp(userSocialSignUpRequest))
                .isInstanceOf(AlreadySignedUpUserException.class);
    }

    @Test
    @DisplayName("기존 회원과 중복된 닉네임으로 회원가입을 시도하면 예외가 발생한다.")
    void duplicateNicknameSignUpRequest() {
        // given
        when(userRepository.findByNickname(userSocialSignUpRequest.getNickname())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> signUpStrategy.signUp(userSocialSignUpRequest))
                .isInstanceOf(NicknameAlreadyInUseException.class);
    }

    @Test
    @DisplayName("필수 약관에 모두 동의하지 않고 회원가입을 시도하면 예외가 발생한다.")
    void doNotConsentAllRequiredTermsSignUpRequest() {
        // given
        doThrow(RequiredTermsNotAgreedException.class).when(termsService).validateRequiredTermsConsents(termsAgreementDto);

        // when
        // then
        assertThatThrownBy(() -> signUpStrategy.signUp(userSocialSignUpRequest))
                .isInstanceOf(RequiredTermsNotAgreedException.class);
    }

    @Test
    @DisplayName("계정의 이메일과 소셜 연동 정보의 이메일이 일치하지 않으면 예외가 발생한다.")
    void socialEmailMismatch() {
        String mismatchedSocialEmail = "social1234@gmail.com";

        UserSocialSignUpRequest request = UserSocialSignUpRequest.builder()
                .email(userSocialSignUpRequest.getEmail())
                .nickname(userSocialSignUpRequest.getNickname())
                .termsAgreementDto(userSocialSignUpRequest.getTermsAgreementDto())
                .socialProfileDto(
                        UserSignUpSocialProfileDto.builder()
                                .socialEmail(mismatchedSocialEmail)
                                .build())
                .build();

        // when
        // then
        assertThatThrownBy(() -> signUpStrategy.signUp(request))
                .isInstanceOf(SocialEmailMismatchException.class);
    }
}