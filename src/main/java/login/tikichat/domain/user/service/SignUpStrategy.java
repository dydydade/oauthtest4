package login.tikichat.domain.user.service;

import login.tikichat.domain.user.dto.BaseUserSignUpRequest;
import login.tikichat.domain.user.dto.UserSignUpResponse;

import java.net.MalformedURLException;

public interface SignUpStrategy {

    UserSignUpResponse signUp(BaseUserSignUpRequest userSignUpRequest) throws MalformedURLException;

    /**
     * [회원가입 정보(이메일, 닉네임 등) 유효성 검증 메서드]
     * @param userSignUpRequest
     */
    void validateSignUpInfo(BaseUserSignUpRequest userSignUpRequest);
}
