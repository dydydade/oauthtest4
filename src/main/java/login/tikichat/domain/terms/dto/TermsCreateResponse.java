package login.tikichat.domain.terms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import login.tikichat.domain.terms.model.TermsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermsCreateResponse {

    @Schema(description = "이용약관 ID")
    private Long id;

    @Schema(description = "이용약관 종류", example = "PRIVACY_POLICY")
    @Enumerated(EnumType.STRING)
    private TermsType termsType;        // 약관 종류

    @Schema(description = "이용약관 버전", example = "1.0")
    private String version;             // 버전

    @Schema(description = "필수 약관 여부", example = "true")
    private boolean mandatory;          // 필수 약관 여부

    @Schema(description = "약관 시행 일자", example = "2024-07-07")
    private LocalDate effectiveDate;    // 약관 시행 일자
}
