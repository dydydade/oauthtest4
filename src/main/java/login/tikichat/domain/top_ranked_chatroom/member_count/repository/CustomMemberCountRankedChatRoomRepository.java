package login.tikichat.domain.top_ranked_chatroom.member_count.repository;

import login.tikichat.domain.top_ranked_chatroom.member_count.model.MemberCountRankedChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomMemberCountRankedChatRoomRepository {
    List<MemberCountRankedChatRoom> findMemberCountChatRooms(String categoryCode, Pageable pageable);
}

