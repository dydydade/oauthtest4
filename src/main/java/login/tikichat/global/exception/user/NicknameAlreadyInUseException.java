package login.tikichat.global.exception.user;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class NicknameAlreadyInUseException extends BusinessException {
    public NicknameAlreadyInUseException() {
        super(ErrorCode.NICKNAME_ALREADY_IN_USE);
    }
}