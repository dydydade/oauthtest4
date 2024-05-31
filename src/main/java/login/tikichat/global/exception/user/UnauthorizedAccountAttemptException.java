package login.tikichat.global.exception.user;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class UnauthorizedAccountAttemptException extends BusinessException {
    public UnauthorizedAccountAttemptException() {
        super(ErrorCode.UNAUTHORIZED_ACCOUNT_ATTEMPT);
    }
}