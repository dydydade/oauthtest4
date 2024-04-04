package login.oauthtest4.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class UserSignUpDto {

    private String username; // 유저가 입력한 ID
    private String email;    // 유저가 입력한 email
    private String password; // 유저가 입력한 password
    private String nickname;
    private LocalDate birthDate;
}
