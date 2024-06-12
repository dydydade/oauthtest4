package login.tikichat.domain.chatroom.repository;

import login.tikichat.domain.chatroom.dto.FindChatRoomDto;
import login.tikichat.domain.chatroom.model.ChatRoom;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomChatRoomRepository {
    List<ChatRoom> findChatRooms(FindChatRoomDto.FindChatRoomReq findChatRoomReq, Long userId);
}