package login.tikichat.domain.terms.model;

import jakarta.persistence.*;
import login.tikichat.global.base.BaseEntity;
import lombok.*;

import java.time.LocalDate;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Terms extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terms_id")
    private Long id;

    private String version;             // 버전

    @Enumerated(EnumType.STRING)
    private TermsType termsType;        // 약관 종류

    @Lob
    private String content;             // 내용

    private boolean mandatory;          // 필수 약관 여부

    private LocalDate effectiveAt;      // 약관 시행 일자
}
