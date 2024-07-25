package login.tikichat.domain.user.service;

import login.tikichat.domain.terms.service.TermsService;
import login.tikichat.domain.user.dto.*;
import login.tikichat.domain.user.model.Role;
import login.tikichat.domain.user.model.SocialProfile;
import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.repository.SocialProfileRepository;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.auth.oauth2.service.CustomOAuth2UserService;
import login.tikichat.global.exception.user.AlreadySignedUpUserException;
import login.tikichat.global.exception.user.NicknameAlreadyInUseException;
import login.tikichat.global.exception.user.SocialEmailMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SocialSignUpStrategy implements SignUpStrategy {

    private final CommonSignUpServices services;
    private final UserRepository userRepository;
    private final SocialProfileRepository socialProfileRepository;
    private final TermsService termsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    /**
     * [회원가입 메서드]
     * @param baseUserSignUpRequest
     * @return
     */
    @Override
    @Transactional
    public UserSignUpResponse signUp(BaseUserSignUpRequest baseUserSignUpRequest) {
        // 회원가입 정보(이메일, 닉네임 등) 유효성 검증
        this.validateSignUpInfo(baseUserSignUpRequest);

        User user = User.builder()
                .email(baseUserSignUpRequest.getEmail())
                .nickname(baseUserSignUpRequest.getNickname())
                .role(Role.SOCIAL)
                .build();

        User savedUser = userRepository.save(user);

        UserSocialProfileDto socialProfileDto = ((UserSocialSignUpRequest) baseUserSignUpRequest).getSocialProfileDto();

        // 소셜 계정 연동정보 저장
        customOAuth2UserService.linkSocialProfile(socialProfileDto, user);

        // 회원가입 필수 약관 동의 이력 저장
        services.saveAgreementHistory(baseUserSignUpRequest, user);

        // 응답 객체 반환
        return services.toSignUpResponse(savedUser);
    }

    /**
     * [회원가입 정보(이메일, 닉네임 등) 유효성 검증 메서드]
     * @param baseUserSignUpRequest
     */
    @Override
    @Transactional(readOnly = true)
    public void validateSignUpInfo(BaseUserSignUpRequest baseUserSignUpRequest) {
        // 기존 회원과 중복된 이메일인지 검증
        if (userRepository.findByEmail(baseUserSignUpRequest.getEmail()).isPresent()) {
            throw new AlreadySignedUpUserException();
        }

        // 기존 회원과 중복된 닉네임인지 검증
        if (userRepository.findByNickname(baseUserSignUpRequest.getNickname()).isPresent()) {
            throw new NicknameAlreadyInUseException();
        }

        // 회원가입 필수 약관 동의 여부 검증
        termsService.validateRequiredTermsConsents(baseUserSignUpRequest.getTermsAgreementDto());

        // 형변환
        UserSocialSignUpRequest userSocialSignUpRequest = (UserSocialSignUpRequest) baseUserSignUpRequest;

        // 요청에 소셜 연동 정보가 포함된 경우: 계정의 이메일과 소셜 이메일이 일치하는지 검증
        if (!userSocialSignUpRequest.getEmail().equals(userSocialSignUpRequest.getSocialProfileDto().getSocialEmail())) {
            throw new SocialEmailMismatchException();
        }
    }
}