package login.tikichat.domain.terms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class TermsCreateRequest {

    @Schema(description = "이용약관 종류", example = "PRIVACY_POLICY")
    @NotNull(message = "약관 종류는 필수 입력값입니다.")
    @Enumerated(EnumType.STRING)
    private TermsType termsType;        // 약관 종류

    @Schema(description = "이용약관 버전", example = "1.0")
    @NotEmpty(message = "약관 버전은 필수 입력값입니다.")
    private String version;             // 버전

    @Schema(description = "이용약관 본문", example = "개인정보 처리방침입니다.")
    @NotEmpty(message = "약관 본문은 필수 입력값입니다.")
    private String content;             // 내용

    @Schema(description = "필수 약관 여부", example = "true")
    @NotNull(message = "필수 약관 여부는 필수 입력값입니다.")
    private boolean mandatory;          // 필수 약관 여부

    @Schema(description = "약관 시행 일자", example = "2024-07-07")
    @NotNull(message = "약관 시행 일자는 필수 입력값입니다.")
    private LocalDate effectiveDate;    // 약관 시행 일자
}
