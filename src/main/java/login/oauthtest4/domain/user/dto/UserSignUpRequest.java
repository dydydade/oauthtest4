package login.oauthtest4.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 형식의 이메일을 입력해 주세요.")
    private String email;    // 유저가 입력한 email

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$",
            message = "비밀번호에는 문자, 숫자, 특수 문자가 각각 1개 이상 포함되어야 합니다. 특수 문자는 !@#$%^&* 만 사용할 수 있으며, 길이는 8~20자여야 합니다.")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password; // 유저가 입력한 password

    private String nickname;

    private UserSignUpSocialProfileDto socialProfileDto;
}


