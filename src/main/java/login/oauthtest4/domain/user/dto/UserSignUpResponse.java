package login.oauthtest4.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpResponse {
    private Long userId;
    private String email;    // 유저가 입력한 email
    private String nickname;
}
