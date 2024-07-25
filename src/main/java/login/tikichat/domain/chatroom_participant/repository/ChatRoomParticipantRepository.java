package login.tikichat.domain.chatroom_participant.repository;

import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom_participant.model.ChatRoomParticipant;
import login.tikichat.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {
    Optional<ChatRoomParticipant> findByUserAndChatRoom(User user, ChatRoom chatRoom);
}
