package login.tikichat.domain.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    SOCIAL("ROLE_SOCIAL"), USER("ROLE_USER"), INFLUENCER("ROLE_INFLUENCER"), ADMIN("ROLE_ADMIN");

    private final String key;
}
