package login.tikichat.domain.host.repository;

import login.tikichat.domain.host.dto.FindHostDto;
import login.tikichat.domain.host.model.Host;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomHostRepository {
    Optional<Host> findByHostUserId(Long hostUserId);

    List<Host> findHosts(FindHostDto.FindHostReq findHostReq);
}
