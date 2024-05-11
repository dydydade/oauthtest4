package login.oauthtest4.domain.user.service;

import jakarta.transaction.Transactional;
import login.oauthtest4.domain.terms.service.TermsService;
import login.oauthtest4.domain.user.dto.UserSignUpRequest;
import login.oauthtest4.domain.user.dto.UserSignUpResponse;
import login.oauthtest4.domain.user.dto.UserSignUpSocialProfileDto;
import login.oauthtest4.domain.user.dto.UserSocialSignUpRequest;
import login.oauthtest4.domain.user.model.Role;
import login.oauthtest4.domain.user.model.SocialProfile;
import login.oauthtest4.domain.user.model.User;
import login.oauthtest4.domain.user.repository.SocialProfileRepository;
import login.oauthtest4.domain.user.repository.UserRepository;
import login.oauthtest4.global.exception.user.AlreadySignedUpUserException;
import login.oauthtest4.global.exception.user.NicknameAlreadyInUseException;
import login.oauthtest4.global.exception.user.SocialEmailMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialSignUpStrategy implements SignUpStrategy {

    private final CommonSignUpServices services;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SocialProfileRepository socialProfileRepository;
    private final TermsService termsService;

//    private static final String TEMP_SOCIAL_PASSWORD = "temp-social-password-lemon157";

    /**
     * [회원가입 메서드]
     * @param userSignUpRequest
     * @return
     */
    @Override
    @Transactional
    public UserSignUpResponse signUp(UserSignUpRequest userSignUpRequest) {
        // 회원가입 정보(이메일, 닉네임 등) 유효성 검증
        this.validateSignUpInfo(userSignUpRequest);

        User user = User.builder()
                .email(userSignUpRequest.getEmail())
//                .password(passwordEncoder.encode(TEMP_SOCIAL_PASSWORD))
                .password(passwordEncoder.encode(userSignUpRequest.getPassword()))
                .nickname(userSignUpRequest.getNickname())
                .role(Role.SOCIAL)
                .build();

        User savedUser = userRepository.save(user);

        // 소셜 계정 연동정보 저장
        this.linkSocialProfile((UserSocialSignUpRequest) userSignUpRequest, user);

        // 회원가입 필수 약관 동의 이력 저장
        services.saveAgreementHistory(userSignUpRequest, user);

        // 응답 객체 반환
        return services.toSignUpResponse(savedUser);
    }

    /**
     * [회원가입 정보(이메일, 닉네임 등) 유효성 검증 메서드]
     * @param userSignUpRequest
     */
    @Override
    @Transactional
    public void validateSignUpInfo(UserSignUpRequest userSignUpRequest) {
        // 기존 회원과 중복된 이메일인지 검증
        if (userRepository.findByEmail(userSignUpRequest.getEmail()).isPresent()) {
            throw new AlreadySignedUpUserException();
        }

        // 기존 회원과 중복된 닉네임인지 검증
        if (userRepository.findByNickname(userSignUpRequest.getNickname()).isPresent()) {
            throw new NicknameAlreadyInUseException();
        }

        // 회원가입 필수 약관 동의 여부 검증
        termsService.validateRequiredTermsConsents(userSignUpRequest.getTermsAgreementDto());

        // 형변환
        UserSocialSignUpRequest userSocialSignUpRequest = (UserSocialSignUpRequest) userSignUpRequest;

        // 요청에 소셜 연동 정보가 포함된 경우: 계정의 이메일과 소셜 이메일이 일치하는지 검증
        if (!userSignUpRequest.getEmail().equals(userSocialSignUpRequest.getSocialProfileDto().getSocialEmail())) {
            throw new SocialEmailMismatchException();
        }
    }

    /**
     * [소셜 프로필 연계 메서드]
     * @param userSignUpRequest
     * @param user
     */
    @Transactional
    private void linkSocialProfile(UserSocialSignUpRequest userSignUpRequest, User user) {
        UserSignUpSocialProfileDto socialProfileDto = userSignUpRequest.getSocialProfileDto();

        SocialProfile socialProfile = SocialProfile.builder()
                .socialId(socialProfileDto.getSocialId())
                .socialEmail(socialProfileDto.getSocialEmail())
                .socialType(socialProfileDto.getSocialType())
                .user(user)
                .build();

        socialProfileRepository.save(socialProfile);
    }
}