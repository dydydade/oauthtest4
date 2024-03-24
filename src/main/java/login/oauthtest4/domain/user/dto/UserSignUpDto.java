package login.oauthtest4.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSignUpDto {

    private String username; // 유저가 입력한 ID
    private String email;    // Authentication 객체의 Principal : email
    private String password; // Authentication 객체의 Credential : password
    private String nickname;
    private int age;
    private String city;
}
