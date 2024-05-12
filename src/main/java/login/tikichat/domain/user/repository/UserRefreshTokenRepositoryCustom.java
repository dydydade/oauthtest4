package login.tikichat.domain.user.repository;

import login.tikichat.domain.user.model.UserRefreshToken;

import java.util.Optional;

public interface UserRefreshTokenRepositoryCustom {

    Optional<UserRefreshToken> findByRefreshToken(String refreshToken);

    Optional<UserRefreshToken> findByUserEmailAndDeviceId(String email, String deviceId);
}
