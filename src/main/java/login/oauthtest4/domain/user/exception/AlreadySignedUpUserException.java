package login.oauthtest4.domain.user.exception;

public class AlreadySignedUpUserException extends RuntimeException {

    public AlreadySignedUpUserException() {
        super("이미 가입한 계정이 있습니다.");
    }

    public AlreadySignedUpUserException(String message) {
        super(message);
    }
}
