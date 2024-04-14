package login.oauthtest4.global.exception.user;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class RegisteredUserNotFoundException extends BusinessException {
    public RegisteredUserNotFoundException() {
        super(ErrorCode.REGISTERED_USER_NOT_FOUND);
    }
}