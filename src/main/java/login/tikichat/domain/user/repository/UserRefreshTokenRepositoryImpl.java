package login.tikichat.domain.user.repository;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import login.tikichat.domain.user.model.QUser;
import login.tikichat.domain.user.model.QUserRefreshToken;
import login.tikichat.domain.user.model.UserRefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRefreshTokenRepositoryImpl implements UserRefreshTokenRepositoryCustom {

    private final EntityManager entityManager;

    private QUser user = QUser.user;

    private QUserRefreshToken userRefreshToken = QUserRefreshToken.userRefreshToken;

    @Override
    public Optional<UserRefreshToken> findByRefreshToken(String refreshToken) {
        JPAQuery<UserRefreshToken> query = new JPAQuery<>(entityManager);

        UserRefreshToken result = query.select(userRefreshToken)
                .from(userRefreshToken)
                .join(userRefreshToken.user, user)
                .fetchJoin()
                .where(userRefreshToken.refreshToken.eq(refreshToken))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<UserRefreshToken> findByUserEmailAndDeviceId(String email, String deviceId) {
        JPAQuery<UserRefreshToken> query = new JPAQuery<>(entityManager);

        UserRefreshToken result = query.select(userRefreshToken)
                .from(userRefreshToken)
                .join(userRefreshToken.user, user)
                .fetchJoin()
                .where(user.email.eq(email)
                        .and(userRefreshToken.deviceId.eq(deviceId)))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
