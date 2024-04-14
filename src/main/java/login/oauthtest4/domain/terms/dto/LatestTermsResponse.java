package login.oauthtest4.domain.terms.dto;


import jakarta.persistence.*;
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
public class LatestTermsResponse {

    private Long id;

    private String version;             // 버전

    @Enumerated(EnumType.STRING)
    private TermsType termsType;        // 약관 종류

    private String content;             // 내용

    private boolean mandatory;          // 필수 약관 여부

    private LocalDate effectiveAt;      // 약관 시행 일자
}
