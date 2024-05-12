package login.tikichat.domain.user.repository;

import login.tikichat.domain.user.model.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long>, UserRefreshTokenRepositoryCustom {

    Optional<UserRefreshToken> findByRefreshToken(String refreshToken);

    Optional<UserRefreshToken> findByUserEmailAndDeviceId(String email, String deviceId);
}
