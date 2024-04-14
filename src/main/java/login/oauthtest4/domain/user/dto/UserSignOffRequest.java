package login.oauthtest4.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignOffRequest {
    @NotEmpty(message = "탈퇴할 계정 ID는 필수값입니다.")
    private Long userId;
}
