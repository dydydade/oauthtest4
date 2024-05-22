package login.oauthtest4.domain.chatroom.service;

import login.oauthtest4.domain.chatroom.dto.CreateChatRoomDto;
import login.oauthtest4.domain.chatroom.model.ChatRoom;
import login.oauthtest4.domain.chatroom.repository.ChatRootRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRootService {
    private final ChatRootRepository chatRootRepository;

    public Long createChatRoom(
            Long rootManagerUserId,
            CreateChatRoomDto.CreateChatRoomReq createChatRoomReq
    ) {
        final var chatRoot = new ChatRoom(
                rootManagerUserId,
                createChatRoomReq.name(),
                createChatRoomReq.maxUserCount(),
                createChatRoomReq.tags()
        );

        this.chatRootRepository.save(chatRoot);

        return chatRoot.getId();
    }
}
