package login.tikichat.domain.user.repository;

import login.tikichat.domain.user.model.QUser;
import login.tikichat.domain.user.model.QUserRefreshToken;
import login.tikichat.domain.user.model.UserRefreshToken;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRefreshTokenRepositoryImpl extends QuerydslRepositorySupport implements UserRefreshTokenRepositoryCustom {
    public UserRefreshTokenRepositoryImpl() {
        super(UserRefreshToken.class);
    }

    @Override
    public Optional<UserRefreshToken> findByRefreshToken(String refreshToken) {
        final var userRefreshTokenQ = QUserRefreshToken.userRefreshToken;
        final var userQ = QUser.user;
        final var query = super.from(userRefreshTokenQ);

        UserRefreshToken result = query
                .join(userRefreshTokenQ.user, userQ)
                .fetchJoin()
                .where(userRefreshTokenQ.refreshToken.eq(refreshToken))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<UserRefreshToken> findByUserEmailAndDeviceId(String email, String deviceId) {
        final var userRefreshTokenQ = QUserRefreshToken.userRefreshToken;
        final var userQ = QUser.user;
        final var query = super.from(userRefreshTokenQ);

        UserRefreshToken result = query.select(userRefreshTokenQ)
                .from(userRefreshTokenQ)
                .join(userRefreshTokenQ.user, userQ)
                .fetchJoin()
                .where(userQ.email.eq(email)
                        .and(userRefreshTokenQ.deviceId.eq(deviceId)))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
