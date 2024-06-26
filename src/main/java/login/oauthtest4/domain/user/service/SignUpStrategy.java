package login.oauthtest4.domain.user.service;

import login.oauthtest4.domain.terms.service.AgreementHistoryService;
import login.oauthtest4.domain.user.dto.UserSignUpRequest;
import login.oauthtest4.domain.user.dto.UserSignUpResponse;
import lombok.RequiredArgsConstructor;

public interface SignUpStrategy {

    UserSignUpResponse signUp(UserSignUpRequest userSignUpRequest);

    /**
     * [회원가입 정보(이메일, 닉네임 등) 유효성 검증 메서드]
     * @param userSignUpRequest
     */
    void validateSignUpInfo(UserSignUpRequest userSignUpRequest);
}
