package login.oauthtest4.global.exception.auth;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class InvalidVerificationTokenException extends BusinessException {
    public InvalidVerificationTokenException() {
        super(ErrorCode.INVALID_VERIFICATION_AUTH_TOKEN);
    }
}