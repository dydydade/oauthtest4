package login.oauthtest4.domain.user.repository;

import login.oauthtest4.domain.user.model.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long>, UserRefreshTokenRepositoryCustom {

    Optional<UserRefreshToken> findByRefreshToken(String refreshToken);

    Optional<UserRefreshToken> findByUserEmailAndDeviceId(String email, String deviceId);
}
