package login.tikichat.domain.host.repository.impl;

import login.tikichat.domain.host.model.HostSubscription;
import login.tikichat.domain.host.model.QHostSubscription;
import login.tikichat.domain.host.repository.CustomHostSubscriptionRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class HostSubscriptionRepositoryImpl extends QuerydslRepositorySupport implements CustomHostSubscriptionRepository {
    public HostSubscriptionRepositoryImpl() {
        super(HostSubscription.class);
    }

    @Override
    public boolean existsByHostIdAndFollowerUserId(Long hostId, Long userId) {

        final var hostSubscriptionQ = QHostSubscription.hostSubscription;
        final var query = super.from(hostSubscriptionQ);

        return query.where(hostSubscriptionQ.host.id.eq(hostId)
                        .and(hostSubscriptionQ.follower.user.id.eq(userId)))
                .fetchCount() > 0;
    }
}
