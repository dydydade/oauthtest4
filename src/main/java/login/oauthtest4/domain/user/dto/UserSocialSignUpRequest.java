package login.oauthtest4.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialSignUpRequest extends BaseUserSignUpRequest {

    @Schema(description = "소셜 로그인 연동 정보")
    @NotNull(message = "소셜 회원가입 시 소셜 로그인 연동 정보를 반드시 포함하여야 합니다.")
    private UserSignUpSocialProfileDto socialProfileDto;
}


