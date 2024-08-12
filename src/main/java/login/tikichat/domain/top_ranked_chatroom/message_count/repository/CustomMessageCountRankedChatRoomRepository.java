package login.tikichat.domain.top_ranked_chatroom.message_count.repository;

import login.tikichat.domain.top_ranked_chatroom.message_count.model.MessageCountRankedChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomMessageCountRankedChatRoomRepository {
    List<MessageCountRankedChatRoom> findMessageCountChatRooms(String categoryCode, Pageable pageable);
}
