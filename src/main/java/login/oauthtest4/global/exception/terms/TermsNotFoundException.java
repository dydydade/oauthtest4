package login.oauthtest4.global.exception.terms;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class TermsNotFoundException extends BusinessException {
    public TermsNotFoundException() {
        super(ErrorCode.TERMS_NOT_FOUND);
    }
}