package login.tikichat.global.exception.terms;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class RequiredTermsNotAgreedException extends BusinessException {
    public RequiredTermsNotAgreedException() {
        super(ErrorCode.REQUIRED_TERMS_NOT_AGREED);
    }
}
