package login.oauthtest4.global.auth.login.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessResponse {

    @Schema(description = "로그인에 성공한 회원 이메일")
    private String email;
}
