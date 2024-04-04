package login.oauthtest4.global.auth.exception;

public class EmailNotFoundException extends RuntimeException {

    public EmailNotFoundException() {
        super("이메일을 찾지 못했습니다.");
    }

    public EmailNotFoundException(String message) {
        super(message);
    }
}
