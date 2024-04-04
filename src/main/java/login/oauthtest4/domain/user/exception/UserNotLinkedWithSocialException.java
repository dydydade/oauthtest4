package login.oauthtest4.domain.user.exception;

public class UserNotLinkedWithSocialException extends RuntimeException {

    public UserNotLinkedWithSocialException() {
        super("이미 가입한 계정이 있습니다. 로그인을 통해 소셜 계정과 연동하세요.");
    }

    public UserNotLinkedWithSocialException(String message) {
        super(message);
    }
}
