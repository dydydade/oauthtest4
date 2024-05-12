package login.tikichat.global.exception.auth;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class InvalidVerificationTokenException extends BusinessException {
    public InvalidVerificationTokenException() {
        super(ErrorCode.INVALID_VERIFICATION_AUTH_TOKEN);
    }
}