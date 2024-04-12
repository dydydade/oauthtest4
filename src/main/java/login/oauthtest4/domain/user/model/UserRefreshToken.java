package login.oauthtest4.domain.user.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserRefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String refreshToken;
    private String deviceId;


    // User와의 연관관계 설정 편의 메서드
    public void setUser(User newUser) {
        // 새로운 User 설정
        this.user = newUser;

        // 새로운 User의 RefreshToken 리스트에 현재 Token이 없다면 추가
        if (newUser != null && !newUser.getUserRefreshTokens().contains(this)) {
            newUser.getUserRefreshTokens().add(this);
        }
    }
}
