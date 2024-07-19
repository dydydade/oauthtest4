package login.tikichat.domain.top_ranked_chatroom.repository;

import login.tikichat.domain.top_ranked_chatroom.model.TopRankedChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopRankedChatRoomRepository extends JpaRepository<TopRankedChatRoom, Long> {
}
