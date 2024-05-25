package login.tikichat.global.exception.user;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class SocialEmailMismatchException extends BusinessException {
    public SocialEmailMismatchException() {
        super(ErrorCode.SOCIAL_EMAIL_MISMATCH);
    }
}