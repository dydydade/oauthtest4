package login.oauthtest4.global.auth.verification.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EmailVerifyResponse {
    private final String token;
}
