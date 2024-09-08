package login.tikichat.domain.chatroom.service;

import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom.repository.ChatRoomRepository;
import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomCommonService {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom findById(Long id) {
        return this.chatRoomRepository.findById(id).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_FOUND_CHAT_ROOM)
        );
    }
}
