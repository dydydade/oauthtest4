package login.oauthtest4.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignOffRequest {

    @Schema(description = "탈퇴할 회원 ID")
    @NotEmpty(message = "탈퇴할 회원 ID는 필수값입니다.")
    private Long userId;
}
