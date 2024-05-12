package login.tikichat.domain.user.service;

import jakarta.transaction.Transactional;
import login.tikichat.domain.terms.service.TermsService;
import login.tikichat.domain.user.dto.UserSignUpRequest;
import login.tikichat.domain.user.dto.UserSignUpResponse;
import login.tikichat.domain.user.model.Role;
import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.exception.user.AlreadySignedUpUserException;
import login.tikichat.global.exception.user.NicknameAlreadyInUseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NormalSignUpStrategy implements SignUpStrategy {

    private final CommonSignUpServices services;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TermsService termsService;

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
                .password(passwordEncoder.encode(userSignUpRequest.getPassword()))
                .nickname(userSignUpRequest.getNickname())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

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
    }
}
