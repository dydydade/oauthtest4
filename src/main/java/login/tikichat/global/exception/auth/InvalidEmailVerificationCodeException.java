package login.tikichat.global.exception.auth;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class InvalidEmailVerificationCodeException extends BusinessException {
    public InvalidEmailVerificationCodeException() {
        super(ErrorCode.INVALID_EMAIL_VERIFICATION_CODE);
    }
}