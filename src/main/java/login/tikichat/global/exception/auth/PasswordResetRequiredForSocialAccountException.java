package login.tikichat.global.exception.auth;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class PasswordResetRequiredForSocialAccountException extends BusinessException {
    public PasswordResetRequiredForSocialAccountException() {
        super(ErrorCode.PASSWORD_RESET_REQUIRED_FOR_SOCIAL_ACCOUNT);
    }
}
