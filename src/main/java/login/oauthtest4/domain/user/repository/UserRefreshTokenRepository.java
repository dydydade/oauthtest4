package login.oauthtest4.domain.user.repository;

import login.oauthtest4.domain.user.model.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

    @Query("select urt from UserRefreshToken urt join fetch urt.user u where urt.refreshToken = :refreshToken")
    Optional<UserRefreshToken> findByRefreshToken(String refreshToken);

    @Query("select urt from UserRefreshToken urt join fetch urt.user u where u.email = :email and urt.deviceId = :deviceId")
    Optional<UserRefreshToken> findByUserEmailAndDeviceId(String email, String deviceId);
}
