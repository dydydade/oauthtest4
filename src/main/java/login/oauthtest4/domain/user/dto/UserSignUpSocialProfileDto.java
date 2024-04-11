package login.oauthtest4.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import login.oauthtest4.domain.user.model.SocialType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpSocialProfileDto {
    @NotEmpty
    private SocialType socialType; // KAKAO, NAVER, GOOGLE, FACEBOOK

    @NotEmpty
    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    @NotEmpty
    private String socialEmail; // 인증 서버로부터 넘겨받은 소셜 email 정보
}
