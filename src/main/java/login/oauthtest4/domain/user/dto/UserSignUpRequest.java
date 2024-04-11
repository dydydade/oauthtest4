package login.oauthtest4.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpRequest {
    private String email;    // 유저가 입력한 email

    private String password; // 유저가 입력한 password

    private String nickname;

    private UserSignUpSocialProfileDto socialProfileDto;
}


