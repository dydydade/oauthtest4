package login.oauthtest4.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordChangeRequest {
    private String tempToken;
    private String email;
    private String newPassword;
}
