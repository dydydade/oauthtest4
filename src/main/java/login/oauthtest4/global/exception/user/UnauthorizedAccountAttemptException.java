package login.oauthtest4.global.exception.user;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class UnauthorizedAccountAttemptException extends BusinessException {
    public UnauthorizedAccountAttemptException() {
        super(ErrorCode.UNAUTHORIZED_ACCOUNT_ATTEMPT);
    }
}