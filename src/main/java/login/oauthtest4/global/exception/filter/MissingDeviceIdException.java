package login.oauthtest4.global.exception.filter;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class MissingDeviceIdException extends BusinessException {
    public MissingDeviceIdException() {
        super(ErrorCode.MISSING_DEVICE_ID);
    }
}