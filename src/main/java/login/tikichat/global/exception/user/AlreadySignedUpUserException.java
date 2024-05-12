package login.tikichat.global.exception.user;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class AlreadySignedUpUserException extends BusinessException {
    public AlreadySignedUpUserException() {
        super(ErrorCode.ALREADY_SIGNED_UP_USER);
    }
}
