package login.tikichat.domain.host.repository;

import login.tikichat.domain.host.model.HostFollowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface HostFollowStatusRepository extends JpaRepository<HostFollowStatus, Long>, CustomHostFollowStatusRepository {
    long countByHostId(Long hostId);
    Optional<HostFollowStatus> findByHostIdAndFollowerId(Long hostId, Long followerId);
}
