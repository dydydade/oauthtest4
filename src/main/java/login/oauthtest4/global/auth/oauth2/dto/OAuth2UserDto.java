package login.oauthtest4.global.auth.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class OAuth2UserDto {
    private boolean userExists;
    private UserDto user;
    private Map<String, Object> socialData;
}