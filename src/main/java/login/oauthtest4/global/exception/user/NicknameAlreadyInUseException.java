package login.oauthtest4.global.exception.user;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class NicknameAlreadyInUseException extends BusinessException {
    public NicknameAlreadyInUseException() {
        super(ErrorCode.NICKNAME_ALREADY_IN_USE);
    }
}