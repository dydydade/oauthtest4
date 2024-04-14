package login.oauthtest4.domain.terms.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import login.oauthtest4.domain.terms.model.TermsType;
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

    @NotEmpty
    @Enumerated(EnumType.STRING)
    private TermsType termsType;        // 약관 종류

    @NotEmpty
    private String version;             // 버전

    @NotEmpty
    private String content;             // 내용

    @NotEmpty
    private boolean mandatory;          // 필수 약관 여부

    @NotEmpty
    private LocalDate effectiveDate;    // 약관 시행 일자
}
