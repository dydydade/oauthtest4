package login.oauthtest4.global.exception.auth;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class IdPasswordLoginNotAllowedForSocialAccountException extends BusinessException {
    public IdPasswordLoginNotAllowedForSocialAccountException() {
        super(ErrorCode.ID_PW_LOGIN_NOT_ALLOWED_FOR_SOCIAL_ACCOUNT);
    }
}
