package login.oauthtest4.domain.user.exception;

public class RegisteredUserNotFoundException extends RuntimeException {

    public RegisteredUserNotFoundException() {
        super("기존에 가입한 계정이 없습니다. 회원가입 페이지로 이동할까요?");
    }

    public RegisteredUserNotFoundException(String message) {
        super(message);
    }
}
