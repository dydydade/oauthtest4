package login.tikichat.domain.host.repository;

import login.tikichat.domain.host.model.HostSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HostSubscriptionRepository extends JpaRepository<HostSubscription, Long>, CustomHostSubscriptionRepository {
    long countByHostId(Long hostId);
}
