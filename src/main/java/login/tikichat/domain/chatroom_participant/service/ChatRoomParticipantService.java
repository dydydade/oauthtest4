package login.tikichat.domain.chatroom_participant.service;

import jakarta.persistence.OptimisticLockException;
import login.tikichat.domain.chatroom.repository.ChatRoomRepository;
import login.tikichat.domain.chatroom_participant.model.ChatRoomParticipant;
import login.tikichat.domain.chatroom_participant.repository.ChatRoomParticipantRepository;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChatRoomParticipantService {
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void joinChatRoom(Long chatRoomId, Long userId) {
        final var chatRoom = this.chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CHAT_ROOM)
        );
        final var user = this.userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_USER)
        );

        final var chatRoomParticipant = new ChatRoomParticipant(user, chatRoom);

        if (chatRoom.getCurrentUserCount() + 1 > chatRoom.getMaxUserCount()) {
            throw new BusinessException(ErrorCode.FULL_PARTICIPANT_CHAT_ROOM);
        }

        try {
            chatRoomRepository.addCurrentUserCount(chatRoom.getId());
        } catch (OptimisticLockException e) {
            throw new BusinessException(ErrorCode.FULL_PARTICIPANT_CHAT_ROOM);
        }

        this.chatRoomParticipantRepository.save(chatRoomParticipant);
    }

    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long userId) {
        final var chatRoom = this.chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CHAT_ROOM)
        );
        final var user = this.userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_USER)
        );

        final var chatRoomParticipant = this.chatRoomParticipantRepository.findByUserAndChatRoom(user, chatRoom).orElseThrow();

        chatRoomRepository.subtractCurrentUserCount(chatRoom.getId());

        this.chatRoomParticipantRepository.delete(chatRoomParticipant);
    }
}
