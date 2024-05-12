package login.tikichat.global.exception.auth;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class IdPasswordLoginNotAllowedForSocialAccountException extends BusinessException {
    public IdPasswordLoginNotAllowedForSocialAccountException() {
        super(ErrorCode.ID_PW_LOGIN_NOT_ALLOWED_FOR_SOCIAL_ACCOUNT);
    }
}
