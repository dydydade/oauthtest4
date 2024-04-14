package login.oauthtest4.global.exception.terms;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class RequiredTermsNotAgreedException extends BusinessException {
    public RequiredTermsNotAgreedException() {
        super(ErrorCode.REQUIRED_TERMS_NOT_AGREED);
    }
}
