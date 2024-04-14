package login.oauthtest4.global.exception.user;

import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorCode;

public class UserNotLinkedWithSocialException extends BusinessException {
    public UserNotLinkedWithSocialException() {
        super(ErrorCode.USER_NOT_LINKED_WITH_SOCIAL);
    }
}