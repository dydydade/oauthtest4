package login.tikichat.domain.user.service;

import login.tikichat.domain.terms.service.AgreementHistoryService;
import login.tikichat.domain.user.dto.BaseUserSignUpRequest;
import login.tikichat.domain.user.dto.UserSignUpResponse;
import login.tikichat.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [회원가입 공통 로직 관리하는 클래스]
 */
@Service
@RequiredArgsConstructor
public class CommonSignUpServices {

    private final AgreementHistoryService agreementHistoryService;

    /**
     * [회원가입 필수 약관 동의 이력 저장하는 메서드]
     * @param baseUserSignUpRequest
     * @param user
     */
    @Transactional
    protected void saveAgreementHistory(BaseUserSignUpRequest baseUserSignUpRequest, User user) {
        agreementHistoryService.saveAgreementHistory(baseUserSignUpRequest.getTermsAgreementDto(), user);
    }

    /**
     * [User 엔티티 → Response 객체로 변환하는 메서드]
     * @param savedUser
     * @return
     */
    protected UserSignUpResponse toSignUpResponse(User savedUser) {
        return UserSignUpResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .build();
    }
}
