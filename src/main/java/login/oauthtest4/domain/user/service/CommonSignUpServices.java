package login.oauthtest4.domain.user.service;

import jakarta.transaction.Transactional;
import login.oauthtest4.domain.terms.service.AgreementHistoryService;
import login.oauthtest4.domain.user.dto.UserSignUpRequest;
import login.oauthtest4.domain.user.dto.UserSignUpResponse;
import login.oauthtest4.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * [회원가입 공통 로직 관리하는 클래스]
 */
@Service
@RequiredArgsConstructor
public class CommonSignUpServices {

    private final AgreementHistoryService agreementHistoryService;

    /**
     * [회원가입 필수 약관 동의 이력 저장하는 메서드]
     * @param userSignUpRequest
     * @param user
     */
    @Transactional
    protected void saveAgreementHistory(UserSignUpRequest userSignUpRequest, User user) {
        agreementHistoryService.saveAgreementHistory(userSignUpRequest.getTermsAgreementDto(), user);
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
