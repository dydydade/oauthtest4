package login.tikichat.domain.host.repository.impl;

import login.tikichat.domain.host.model.Host;
import login.tikichat.domain.host.model.QHost;
import login.tikichat.domain.host.repository.CustomHostRepository;
import login.tikichat.domain.user.model.QUser;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class HostRepositoryImpl extends QuerydslRepositorySupport implements CustomHostRepository {
    public HostRepositoryImpl() {
        super(Host.class);
    }

    @Override
    public Optional<Host> findByHostUserId(Long hostUserId) {
        final var hostQ = QHost.host;
        final var userQ = QUser.user;
        final var query = super.from(hostQ);

        Host host = query.join(hostQ.user, userQ).fetchJoin()
                .where(hostQ.user.id.eq(hostUserId))
                .fetchOne();

        return Optional.ofNullable(host);
    }
}
