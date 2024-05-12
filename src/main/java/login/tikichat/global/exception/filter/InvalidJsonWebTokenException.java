package login.tikichat.global.exception.filter;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class InvalidJsonWebTokenException extends BusinessException {
    public InvalidJsonWebTokenException() {
        super(ErrorCode.INVALID_JSON_WEB_TOKEN);
    }
}