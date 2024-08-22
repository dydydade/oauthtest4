package login.tikichat.domain.chat.repository;

import login.tikichat.domain.chat.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CustomChatRepository {
    List<Chat> findChats(Long chatRoomId, Integer take, Long nextCursor);

    Page<Chat> findAllByCreatedDateBetween(Instant startDate, Instant endDate, Pageable pageable);
}
