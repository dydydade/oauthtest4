package login.oauthtest4.global.auth.verification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NormalLoginRequest {

    @Schema(description = "로그인 이메일")
    @NotEmpty(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(description = "로그인 비밀번호")
    @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
