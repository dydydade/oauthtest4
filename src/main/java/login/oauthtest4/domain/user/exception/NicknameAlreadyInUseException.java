package login.oauthtest4.domain.user.exception;

public class NicknameAlreadyInUseException extends RuntimeException {

    public NicknameAlreadyInUseException() {
        super("이미 사용 중인 닉네임입니다.");
    }

    public NicknameAlreadyInUseException(String message) {
        super(message);
    }
}
