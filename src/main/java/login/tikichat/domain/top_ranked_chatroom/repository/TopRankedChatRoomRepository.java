package login.tikichat.domain.top_ranked_chatroom.repository;

import login.tikichat.domain.top_ranked_chatroom.model.TopRankedChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
public interface TopRankedChatRoomRepository extends JpaRepository<TopRankedChatRoom, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM TopRankedChatRoom t WHERE t.reportDate < :cutoffDate")
    void deleteAllOlderThanCutoffDays(Instant cutoffDate);

    boolean existsByReportDateBetween(Instant startOfDay, Instant endOfDay);
}
