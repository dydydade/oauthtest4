package login.tikichat.domain.terms.model;

public enum TermsType {
    TERMS_OF_SERVICE("서비스 이용약관"),   // 서비스의 기본 이용 조건 및 정책
    PRIVACY_POLICY("개인정보 처리방침"),   // 사용자의 개인 정보 수집 및 이용에 대한 정책
    COOKIE_POLICY("쿠키 정책"),           // 사이트에서 쿠키 사용에 대한 정보와 관리 방법
    MARKETING_PREFERENCES("마케팅 활용 동의"), // 마케팅 목적으로 사용자 정보를 사용하는 것에 대한 동의
    AGE_VERIFICATION("연령 확인");          // 연령 제한 서비스 이용을 위한 확인 절차

    private final String description;

    TermsType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
