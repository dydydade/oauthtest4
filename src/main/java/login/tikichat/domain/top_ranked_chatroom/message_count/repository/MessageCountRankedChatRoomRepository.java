package login.tikichat.domain.top_ranked_chatroom.message_count.repository;

import login.tikichat.domain.top_ranked_chatroom.message_count.model.MessageCountRankedChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
public interface MessageCountRankedChatRoomRepository extends JpaRepository<MessageCountRankedChatRoom, Long>, CustomMessageCountRankedChatRoomRepository {

    @Transactional
    @Modifying
    @Query("DELETE FROM MessageCountRankedChatRoom m WHERE m.reportDate < :cutoffDate")
    void deleteAllOlderThanCutoffDays(Instant cutoffDate);

    boolean existsByReportDateBetween(Instant startOfDay, Instant endOfDay);
}
