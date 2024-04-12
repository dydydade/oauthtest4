package login.oauthtest4.global.auth.login.exception;

public class MissingDeviceIdException extends RuntimeException {

    public MissingDeviceIdException() {
        super("요청 헤더에 Device-ID 정보가 포함되지 않았습니다.");
    }

    public MissingDeviceIdException(String message) {
        super(message);
    }
}
