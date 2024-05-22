package login.oauthtest4.domain.chatroom.repository;

import login.oauthtest4.domain.chatroom.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRootRepository extends JpaRepository<ChatRoom, Long> {
}
