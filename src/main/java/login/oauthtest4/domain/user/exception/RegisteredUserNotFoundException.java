package login.oauthtest4.domain.user.exception;

public class RegisteredUserNotFoundException extends RuntimeException {

    public RegisteredUserNotFoundException() {
        super("가입된 계정을 찾을 수 없습니다.");
    }

    public RegisteredUserNotFoundException(String message) {
        super(message);
    }
}
