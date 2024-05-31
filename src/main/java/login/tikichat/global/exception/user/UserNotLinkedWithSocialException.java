package login.tikichat.global.exception.user;

import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;

public class UserNotLinkedWithSocialException extends BusinessException {
    public UserNotLinkedWithSocialException() {
        super(ErrorCode.USER_NOT_LINKED_WITH_SOCIAL);
    }
}