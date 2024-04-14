package login.oauthtest4.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserSignUpTermsAgreementDto {

    private List<TermsAgreement> agreements;

    @Getter
    @NoArgsConstructor
    public static class TermsAgreement {

        @NotEmpty
        private Long termsId;                       // 대상 약관 ID

        @NotEmpty
        private boolean consentGiven;               // 약관 동의 여부

        @NotEmpty
        private String ipAddress;                   // 동의를 기록한 IP 주소

        @NotEmpty
        private String deviceInformation;           // 동의를 기록한 장치 정보
    }
}