package login.oauthtest4.domain.terms.model;

import jakarta.persistence.*;
import login.oauthtest4.domain.user.model.User;
import login.oauthtest4.global.base.BaseEntity;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AgreementHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agreement_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_id")
    private Terms terms;

    private boolean consentGiven;               // 약관 동의 여부
    private String ipAddress;                   // 동의를 기록한 IP 주소
    private String deviceInformation;           // 동의를 기록한 장치 정보
}
