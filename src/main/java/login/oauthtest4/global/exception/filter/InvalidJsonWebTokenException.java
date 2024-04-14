package login.oauthtest4.global.exception.filter;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class InvalidJsonWebTokenException extends BusinessException {
    public InvalidJsonWebTokenException() {
        super(ErrorCode.INVALID_JSON_WEB_TOKEN);
    }
}