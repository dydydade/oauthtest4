package login.oauthtest4.global.exception.auth;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class PasswordResetRequiredForSocialAccountException extends BusinessException {
    public PasswordResetRequiredForSocialAccountException() {
        super(ErrorCode.PASSWORD_RESET_REQUIRED_FOR_SOCIAL_ACCOUNT);
    }
}
