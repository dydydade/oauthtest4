package login.tikichat.domain.user.repository;

import login.tikichat.domain.user.model.QSocialProfile;
import login.tikichat.domain.user.model.QUser;
import login.tikichat.domain.user.model.SocialProfile;
import login.tikichat.domain.user.model.SocialType;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SocialProfileRepositoryImpl extends QuerydslRepositorySupport implements SocialProfileRepositoryCustom {
    public SocialProfileRepositoryImpl() {
        super(SocialProfile.class);
    }

    @Override
    public Optional<SocialProfile> findBySocialEmailAndSocialTypeWithUser(String email, SocialType socialType) {
        final var socialProfileQ = QSocialProfile.socialProfile;
        final var userQ = QUser.user;
        final var query = super.from(socialProfileQ);

        SocialProfile result = query
                .join(socialProfileQ.user, userQ)
                .fetchJoin()
                .where(socialProfileQ.socialEmail.eq(email).and(socialProfileQ.socialType.eq(socialType)))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
