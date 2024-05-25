package login.tikichat.domain.user.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SocialProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_profile_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE, FACEBOOK, APPLE

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String socialEmail; // 인증 서버로부터 넘겨받은 소셜 email 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
