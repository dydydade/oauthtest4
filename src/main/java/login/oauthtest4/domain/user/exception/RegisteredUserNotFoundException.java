package login.oauthtest4.domain.user.exception;

public class RegisteredUserNotFoundException extends RuntimeException {

    public RegisteredUserNotFoundException() {
        super("기존에 가입한 계정이 없습니다.");
    }

    public RegisteredUserNotFoundException(String message) {
        super(message);
    }
}
