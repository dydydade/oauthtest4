package login.tikichat.global.auth.oauth2.dto;

import lombok.Data;

@Data
public class SocialLoginRequest {
    private String oauth2AccessToken;
}