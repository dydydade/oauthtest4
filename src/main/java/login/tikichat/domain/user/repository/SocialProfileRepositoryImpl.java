package login.tikichat.domain.user.repository;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import login.tikichat.domain.user.model.QSocialProfile;
import login.tikichat.domain.user.model.QUser;
import login.tikichat.domain.user.model.SocialProfile;
import login.tikichat.domain.user.model.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SocialProfileRepositoryImpl implements SocialProfileRepositoryCustom {

    private final EntityManager entityManager;

    private QSocialProfile socialProfile = QSocialProfile.socialProfile;

    private QUser user = QUser.user;

    @Override
    public Optional<SocialProfile> findBySocialEmailAndSocialTypeWithUser(String email, SocialType socialType) {
        JPAQuery<SocialProfile> query = new JPAQuery<>(entityManager);

        SocialProfile result = query.select(socialProfile)
                .from(socialProfile)
                .join(socialProfile.user, user)
                .fetchJoin()
                .where(socialProfile.socialEmail.eq(email).and(socialProfile.socialType.eq(socialType)))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
