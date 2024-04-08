package login.oauthtest4.domain.user.dto;

import login.oauthtest4.domain.user.SocialType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpSocialProfileDto {
    private SocialType socialType; // KAKAO, NAVER, GOOGLE, FACEBOOK

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String socialEmail; // 인증 서버로부터 넘겨받은 소셜 email 정보
}
