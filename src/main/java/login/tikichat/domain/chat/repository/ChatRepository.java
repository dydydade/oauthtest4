package login.tikichat.domain.chat.repository;

import login.tikichat.domain.chat.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long>, CustomChatRepository {
    Page<Chat> findAllByCreatedDateBetween(Instant startDate, Instant endDate, Pageable pageable);
}
