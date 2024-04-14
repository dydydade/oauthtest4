package login.oauthtest4.global.exception.auth;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class EmailNotFoundException extends BusinessException {
    public EmailNotFoundException() {
        super(ErrorCode.EMAIL_NOT_FOUND);
    }
}
