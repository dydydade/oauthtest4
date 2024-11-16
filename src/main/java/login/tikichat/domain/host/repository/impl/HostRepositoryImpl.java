package login.tikichat.domain.host.repository.impl;

import io.micrometer.common.util.StringUtils;
import login.tikichat.domain.host.dto.FindHostDto;
import login.tikichat.domain.host.model.Host;
import login.tikichat.domain.host.model.QHost;
import login.tikichat.domain.host.model.QHostFollowStatus;
import login.tikichat.domain.host.repository.CustomHostRepository;
import login.tikichat.domain.user.model.QUser;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Override
    public List<Host> findHosts(FindHostDto.FindHostReq findHostReq) {
        final var hostQ = QHost.host;
        final var hostFollowStatusQ = QHostFollowStatus.hostFollowStatus;
        final var query = super.from(hostQ);

        if (StringUtils.isNotEmpty(findHostReq.searchKeyword())) {
            query
                .where(hostQ.user.nickname.contains(findHostReq.searchKeyword()));
        }

        if (findHostReq.followerId() != null) {
            query
                .where(hostQ.hostFollowStatuses.any()
                        .follower.id.eq(findHostReq.followerId()));
        }

        return query.fetch();
    }
}
