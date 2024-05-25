package login.tikichat.global.exception.filter;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class MissingDeviceIdException extends BusinessException {
    public MissingDeviceIdException() {
        super(ErrorCode.MISSING_DEVICE_ID);
    }
}