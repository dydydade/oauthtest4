package login.tikichat.global.exception.terms;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class TermsNotFoundException extends BusinessException {
    public TermsNotFoundException() {
        super(ErrorCode.TERMS_NOT_FOUND);
    }
}