package login.tikichat.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordChangeRequest {

    @Schema(description = "비밀번호 설정용 임시 토큰", example = ".....")
    private String tempToken;

    @Schema(description = "새로운 비밀번호", example = "P@ssw0rd12")
    private String newPassword;
}
