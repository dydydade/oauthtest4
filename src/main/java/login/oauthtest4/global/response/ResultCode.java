package login.oauthtest4.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    // User
    NORMAL_REGISTER_SUCCESS(200, "U001", "일반 회원 가입을 완료하였습니다."),
    SOCIAL_REGISTER_SUCCESS(200, "U002", "소셜 회원 가입을 완료하였습니다."),
    MEMBER_WITHDRAWAL_SUCCESS(200, "U003", "회원 탈퇴를 완료하였습니다."),
    NICKNAME_AVAILABLE_SUCCESS(200, "U004", "사용 가능한 닉네임입니다."),
    FIND_USER_INFO_SUCCESS(200, "U004", "회원 정보 조회에 성공하였습니다."),
    LOGIN_SUCCESS(200, "U005", "로그인에 성공하였습니다."),

    // Auth
    PASSWORD_SET_SUCCESS(200, "A001", "비밀번호 설정을 완료하였습니다."),
    AUTH_CODE_SENT_SUCCESS(200, "A002", "인증 코드를 메일로 발송하였습니다."),
    EMAIL_VERIFICATION_SUCCESS(200, "A003", "이메일 인증에 성공하였습니다."),
    TOKEN_ISSUANCE_SUCCESS(200, "A004", "토큰 발급을 완료하였습니다."),
    SOCIAL_EMAIL_NOT_REGISTERED(303, "A005", "기존에 회원으로 존재하지 않는 소셜 이메일입니다. 소셜 회원가입을 진행해 주세요."),
    SOCIAL_LOGIN_SUCCESS(200, "A006", "소셜 로그인에 성공하였습니다."),

    // Terms
    TERMS_REGISTRATION_SUCCESS(200, "T001", "이용 약관을 등록하였습니다."),
    TERMS_LIST_RETRIEVED_SUCCESS(200, "T002", "최신 이용약관 목록을 조회하였습니다.");

    private int status;
    private final String code;
    private final String message;
}