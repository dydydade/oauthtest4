package login.oauthtest4.global.jwt.exception;

public class InvalidJsonWebTokenException extends RuntimeException {

    public InvalidJsonWebTokenException() {
        super("유효하지 않은 토큰입니다.");
    }

    public InvalidJsonWebTokenException(String message) {
        super(message);
    }
}
