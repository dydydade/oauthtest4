package login.tikichat.domain.host.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomHostSubscriptionRepository {
    boolean existsByHostIdAndFollowerUserId(Long hostId, Long userId);
}
