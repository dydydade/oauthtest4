package login.tikichat.domain.user.service;

import login.tikichat.domain.host.model.Follower;
import login.tikichat.domain.host.repository.FollowerRepository;
import login.tikichat.domain.host.model.Host;
import login.tikichat.domain.host.repository.HostRepository;
import login.tikichat.domain.terms.service.TermsService;
import login.tikichat.domain.user.dto.BaseUserSignUpRequest;
import login.tikichat.domain.user.dto.UserNormalSignUpRequest;
import login.tikichat.domain.user.dto.UserSignUpResponse;
import login.tikichat.domain.user.model.Role;
import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.exception.user.AlreadySignedUpUserException;
import login.tikichat.global.exception.user.NicknameAlreadyInUseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NormalSignUpStrategy implements SignUpStrategy {

    private final CommonSignUpServices services;
    private final UserRepository userRepository;
    private final TermsService termsService;
    private final HostRepository hostRepository;
    private final FollowerRepository followerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * [회원가입 메서드]
     * @param baseUserSignUpRequest
     * @return
     */
    @Override
    @Transactional
    public UserSignUpResponse signUp(BaseUserSignUpRequest baseUserSignUpRequest) {
        UserNormalSignUpRequest userNormalSignUpRequest = (UserNormalSignUpRequest) baseUserSignUpRequest;

        // 회원가입 정보(이메일, 닉네임 등) 유효성 검증
        this.validateSignUpInfo(userNormalSignUpRequest);

        User user = createUserEntity(userNormalSignUpRequest);

        // 호스트, 팔로워 정보 저장
        createHostEntity(user);
        createFollowerEntity(user);

        // 회원가입 필수 약관 동의 이력 저장
        services.saveAgreementHistory(userNormalSignUpRequest, user);

        // 응답 객체 반환
        return services.toSignUpResponse(user);
    }

    private User createUserEntity(UserNormalSignUpRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    private void createFollowerEntity(User savedUser) {
        Follower follower = Follower.builder()
                .user(savedUser)
                .build();
        followerRepository.save(follower);
    }

    private void createHostEntity(User savedUser) {
        Host host = Host.builder()
                .user(savedUser)
                .build();
        hostRepository.save(host);
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
    }
}
