package login.oauthtest4.global.auth.exception;

public class InvalidCertificationNumberException extends RuntimeException {

    public InvalidCertificationNumberException() {
        super("유효하지 않은 인증코드입니다.");
    }

    public InvalidCertificationNumberException(String message) {
        super(message);
    }
}
