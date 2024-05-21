package login.tikichat.global.exception.auth;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class EmailVerificationCodeSendLimitExceededException extends BusinessException {
    public EmailVerificationCodeSendLimitExceededException() {
        super(ErrorCode.EMAIL_VERIFICATION_CODE_SEND_LIMIT_EXCEEDED);
    }
}
