package login.tikichat.global.exception.user;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class RegisteredUserNotFoundException extends BusinessException {
    public RegisteredUserNotFoundException() {
        super(ErrorCode.REGISTERED_USER_NOT_FOUND);
    }
}