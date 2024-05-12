package login.oauthtest4.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserNormalSignUpRequest extends BaseUserSignUpRequest {

    @Schema(description = "회원가입 요청 비밀번호")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$",
            message = "비밀번호에는 문자, 숫자, 특수 문자가 각각 1개 이상 포함되어야 합니다. 특수 문자는 !@#$%^&* 만 사용할 수 있으며, 길이는 8~20자여야 합니다.")
    @NotBlank(message = "요청에는 비밀번호를 반드시 포함하여야 합니다.")
    private String password; // 유저가 입력한 password
}


