package login.oauthtest4.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(500, "C001", "internal server error"),
    INVALID_INPUT_VALUE(400, "C002", "invalid input type"),
    METHOD_NOT_ALLOWED(405, "C003", "method not allowed"),
    INVALID_TYPE_VALUE(400, "C004", "invalid type value"),
    BAD_CREDENTIALS(400, "C005", "bad credentials"),

    // Auth
    INVALID_JSON_WEB_TOKEN(400, "A001", "유효하지 않은 토큰입니다."),
    CHECK_EMAIL_OR_PASSWORD(400, "A002", "이메일 또는 비밀번호를 다시 확인해주세요."),
    MISSING_DEVICE_ID(400, "A003", "요청 헤더에 Device-ID 정보가 포함되지 않았습니다."),

    // Terms
    REQUIRED_TERMS_NOT_AGREED(400, "T001", "요청에 필수 약관 정보를 모두 포함시켜 주세요."),
    TERMS_NOT_FOUND(404, "T002", "대상 약관을 찾을 수 없습니다."),

    // User
    ALREADY_SIGNED_UP_USER(409, "U001", "이미 가입한 계정이 있습니다."),
    NICKNAME_ALREADY_IN_USE(409, "U002", "이미 사용 중인 닉네임입니다."),
    REGISTERED_USER_NOT_FOUND(404, "U003", "가입된 계정을 찾을 수 없습니다."),
    SOCIAL_EMAIL_MISMATCH(400, "U004", "계정의 이메일과 소셜 연동 정보의 이메일이 일치하지 않습니다."),
    UNAUTHORIZED_ACCOUNT_ATTEMPT(403, "U005", "요청한 계정과 로그인 사용자가 일치하지 않습니다."),
    USER_NOT_LINKED_WITH_SOCIAL(400, "U006", "이미 가입한 계정이 있습니다. 로그인을 통해 소셜 계정과 연동하세요.");


    private int status;
    private final String code;
    private final String message;
}
