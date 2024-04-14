package login.oauthtest4.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import login.oauthtest4.domain.user.model.SocialType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpSocialProfileDto {

    @Schema(description = "연동된 소셜 로그인 서비스 종류")
    @NotEmpty
    private SocialType socialType; // KAKAO, NAVER, GOOGLE, FACEBOOK

    @Schema(description = "연동된 소셜 로그인 서비스의 식별자")
    @NotEmpty
    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    @Schema(description = "연동된 소셜 로그인 서비스의 이메일 정보")
    @NotEmpty
    private String socialEmail; // 인증 서버로부터 넘겨받은 소셜 email 정보
}
