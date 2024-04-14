package login.oauthtest4.global.exception.user;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class AlreadySignedUpUserException extends BusinessException {
    public AlreadySignedUpUserException() {
        super(ErrorCode.ALREADY_SIGNED_UP_USER);
    }
}
