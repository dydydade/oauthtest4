package login.oauthtest4.domain.user.model;

import jakarta.persistence.*;
import login.oauthtest4.domain.terms.model.AgreementHistory;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Builder
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email; // 이메일
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private String imageUrl; // 프로필 이미지

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SocialProfile> socialProfiles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserRefreshToken> userRefreshTokens; // 리프레시 토큰

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AgreementHistory> termsAgreementHistories;

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = Role.USER;
    }

    //== 유저 필드 업데이트 ==//
    public void updateNickname(String updateNickname) {
        this.nickname = updateNickname;
    }

    public void updatePassword(String updatePassword) {
        this.password = updatePassword;
    }

    // 연관관계 편의 메소드
    public void addUserRefreshToken(UserRefreshToken userRefreshToken) {
        // 현재 User의 RefreshToken 리스트에 추가하고, RefreshToken의 User를 현재 User로 설정
        if (!this.userRefreshTokens.contains(userRefreshToken)) {
            this.userRefreshTokens.add(userRefreshToken);
            userRefreshToken.setUser(this);
        }
    }

    public void removeUserRefreshToken(UserRefreshToken userRefreshToken) {
        // 현재 User의 RefreshToken 리스트에서 제거하고, RefreshToken의 User 연결을 끊음
        if (this.userRefreshTokens.remove(userRefreshToken)) {
            userRefreshToken.setUser(null);
        }
    }
}
