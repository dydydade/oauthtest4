package login.oauthtest4.global.exception.auth;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class InvalidEmailVerificationCodeException extends BusinessException {
    public InvalidEmailVerificationCodeException() {
        super(ErrorCode.INVALID_EMAIL_VERIFICATION_CODE);
    }
}