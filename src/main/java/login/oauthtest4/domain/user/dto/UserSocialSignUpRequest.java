package login.oauthtest4.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSocialSignUpRequest extends UserSignUpRequest {

    @NotNull(message = "소셜 회원가입 시 소셜 계정 연동 정보를 반드시 포함하여야 합니다.")
    private UserSignUpSocialProfileDto socialProfileDto;
}


