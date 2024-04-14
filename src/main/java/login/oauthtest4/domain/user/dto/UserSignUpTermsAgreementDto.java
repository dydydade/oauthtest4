package login.oauthtest4.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserSignUpTermsAgreementDto {

    @Schema(description = "이용약관 동의 정보 목록")
    private List<TermsAgreement> agreements;

    @Getter
    @NoArgsConstructor
    public static class TermsAgreement {

        @Schema(description = "이용약관 ID")
        @NotEmpty
        private Long termsId;                       // 대상 약관 ID

        @Schema(description = "이용약관 동의 여부", example = "true")
        @NotEmpty
        private boolean consentGiven;               // 약관 동의 여부

        @Schema(description = "동의를 기록한 IP 주소", example = "10.241.127.31")
        @NotEmpty
        private String ipAddress;                   // 동의를 기록한 IP 주소

        @Schema(description = "동의를 기록한 기기 정보", example = "용찬 의 iPhone 13")
        @NotEmpty
        private String deviceInformation;           // 동의를 기록한 기기 정보
    }
}