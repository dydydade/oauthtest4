package login.tikichat.domain.chatroom.repository;

import login.tikichat.domain.chatroom.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, CustomChatRoomRepository {
}
