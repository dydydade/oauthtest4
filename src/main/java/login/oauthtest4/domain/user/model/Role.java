package login.oauthtest4.domain.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST"), USER("ROLE_USER"), INFLUENCER("ROLE_INFLUENCER"), ADMIN("ROLE_ADMIN");

    private final String key;
}
