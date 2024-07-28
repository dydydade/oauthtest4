package login.tikichat.domain.top_ranked_chatroom.member_count.repository;

import login.tikichat.domain.top_ranked_chatroom.member_count.model.MemberCountRankedChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
public interface MemberCountRankedChatRoomRepository extends JpaRepository<MemberCountRankedChatRoom, Long>, CustomMemberCountRankedChatRoomRepository {

    @Transactional
    @Modifying
    @Query("DELETE FROM MemberCountRankedChatRoom m WHERE m.reportDate < :cutoffDate")
    void deleteAllOlderThanCutoffDays(Instant cutoffDate);

    boolean existsByReportDateBetween(Instant startOfDay, Instant endOfDay);
}
