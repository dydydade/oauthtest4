package login.oauthtest4.global.exception.user;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class SocialEmailMismatchException extends BusinessException {
    public SocialEmailMismatchException() {
        super(ErrorCode.SOCIAL_EMAIL_MISMATCH);
    }
}