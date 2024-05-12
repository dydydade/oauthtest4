package login.tikichat.global.auth.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String email; // 이메일
    private String nickname; // 닉네임
    private URL imageUrl; // 프로필 이미지
}
