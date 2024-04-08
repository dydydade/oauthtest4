package login.oauthtest4.domain.user.exception;

public class UnauthorizedAccountAttemptException extends RuntimeException {

    public UnauthorizedAccountAttemptException() {
        super("요청한 계정과 로그인 사용자가 일치하지 않습니다.");
    }

    public UnauthorizedAccountAttemptException(String message) {
        super(message);
    }
}
