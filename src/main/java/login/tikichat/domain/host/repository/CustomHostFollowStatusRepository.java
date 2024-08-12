package login.tikichat.domain.host.repository;

import login.tikichat.domain.host.model.HostFollowStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomHostFollowStatusRepository {
    boolean existsByHostIdAndFollowerUserId(Long hostId, Long userId);
    List<HostFollowStatus> findByFollowerUserId(Long userId);
    List<HostFollowStatus> findByHostUserId(Long userId);
}
