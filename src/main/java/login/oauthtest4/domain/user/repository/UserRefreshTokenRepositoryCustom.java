package login.oauthtest4.domain.user.repository;

import login.oauthtest4.domain.user.model.UserRefreshToken;

import java.util.Optional;

public interface UserRefreshTokenRepositoryCustom {

    Optional<UserRefreshToken> findByRefreshToken(String refreshToken);

    Optional<UserRefreshToken> findByUserEmailAndDeviceId(String email, String deviceId);
}
