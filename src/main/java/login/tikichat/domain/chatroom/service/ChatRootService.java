package login.tikichat.domain.chatroom.service;

import login.tikichat.domain.category.repository.CategoryRepository;
import login.tikichat.domain.chatroom.dto.CreateChatRoomDto;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRootService {
    private final ChatRoomRepository chatRoomRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Long createChatRoom(
            Long rootManagerUserId,
            CreateChatRoomDto.CreateChatRoomReq createChatRoomReq
    ) {
        final var category = this.categoryRepository.findByCode(
                createChatRoomReq.categoryCode()
        ).orElseThrow();

        final var chatRoot = new ChatRoom(
                rootManagerUserId,
                createChatRoomReq.name(),
                createChatRoomReq.maxUserCount(),
                createChatRoomReq.tags(),
                category
        );

        this.chatRoomRepository.save(chatRoot);

        return chatRoot.getId();
    }
}
