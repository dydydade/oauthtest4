package login.tikichat.domain.chat.service;

import login.tikichat.domain.chat.constants.ChatReactionType;
import login.tikichat.domain.chat.dto.ModifyReactionChatEventDto;
import login.tikichat.domain.chat.model.ChatReaction;
import login.tikichat.domain.chat.pubsub.SendChatProducer;
import login.tikichat.domain.chat.dto.FindChatsDto;
import login.tikichat.domain.chat.dto.SendChatEventDto;
import login.tikichat.domain.chat.dto.SendMessageDto;
import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.chat.repository.ChatReactionRepository;
import login.tikichat.domain.chat.repository.ChatRepository;
import login.tikichat.domain.chatroom.repository.ChatRoomRepository;
import login.tikichat.domain.chatroom_participant.repository.ChatRoomParticipantRepository;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SendChatProducer sendChatProducer;
    private final ChatReactionRepository chatReactionRepository;

    @Transactional
    public SendMessageDto.SendMessageResDto sendMessage(
            Long userId,
            Long chatRoomId,
            SendMessageDto.SendMessageReqDto sendMessageReqDto
    ) {
        final var user = this.userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_FOUND_USER)
        );
        final var chatRoom = this.chatRoomRepository.findById(chatRoomId).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_FOUND_CHAT_ROOM)
        );

        this.chatRoomParticipantRepository.findByUserAndChatRoom(
                user,
                chatRoom
        ).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_CHAT_ROOM_PARTICIPANT)
        );

        final var chat = Chat.sendMessage(
                userId,
                chatRoom,
                sendMessageReqDto.content()
        );

        this.chatRepository.saveAndFlush(chat);

        this.sendChatProducer.sendMessage(
                new SendChatEventDto(
                    chat.getId(),
                    chat.getContent(),
                    chat.getSenderUserId(),
                    chat.getChatRoom().getId(),
                    chat.getCreatedDate()
                )
        );

        return new SendMessageDto.SendMessageResDto(
                chat.getId(),
                chat.getContent(),
                chat.getCreatedDate()
        );
    }

    public FindChatsDto.FindChatsRes findChats(
            Long userId,
            Long chatRoomId,
            FindChatsDto.FindChatsReq findChatsReq
    ) {
        final var user = this.userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_FOUND_USER)
        );
        final var chatRoom = this.chatRoomRepository.findById(chatRoomId).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_FOUND_CHAT_ROOM)
        );

        this.chatRoomParticipantRepository.findByUserAndChatRoom(
                user,
                chatRoom
        ).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_CHAT_ROOM_PARTICIPANT)
        );

        final var chats = this.chatRepository.findChats(
                chatRoomId,
                findChatsReq.take(),
                findChatsReq.nextCursor()
        );

        return new FindChatsDto.FindChatsRes(
                chats.stream().map((chat) -> {
                    final var chatReactions = chat.getChatReactions();
                    final var chatCounts = new HashMap<ChatReactionType, List<Long>>();

                    for (final var chatReaction : chatReactions) {
                        chatCounts.computeIfAbsent(
                                chatReaction.getChatReactionType(),
                                (k) -> new ArrayList<>()
                        ).add(chatReaction.getUser().getId());
                    }

                    final List<FindChatsDto.FindChatReactionListRes> reactions = new ArrayList<>();
                    chatCounts.forEach((chatReactionType, list) -> {
                                reactions.add(new FindChatsDto.FindChatReactionListRes(
                                        list,
                                        chatReactionType,
                                        list.size()
                                ));
                            }
                    );

                    return new FindChatsDto.FindChatsItemRes(
                            chat.getId(),
                            chat.getContent(),
                            chat.getCreatedDate(),
                            reactions
                    );
                }
                ).toList(),
                chats.isEmpty() ? null : chats.get(chats.size()-1).getId()
        );
    }

    @Transactional
    public Long addChatReaction(Long chatId,
                                Long userId,
                                ChatReactionType chatReactionType
    ) {
        final var user = this.userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));
        final var chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CHAT));

        this.chatRoomParticipantRepository.findByUserAndChatRoom(
                user,
                chat.getChatRoom()
        ).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_CHAT_ROOM_PARTICIPANT)
        );

        if (this.chatReactionRepository.findByChatIdAndUserIdAndChatReactionType(
                chat.getId(),
                user.getId(),
                chatReactionType
        ).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS_REACTION_CHAT);
        };

        final var chatReaction = new ChatReaction(user, chat, chatReactionType);

        chatReactionRepository.save(chatReaction);

        this.sendChatProducer.modifyReaction(
                new ModifyReactionChatEventDto(
                        chat.getId(),
                        chatReaction.getChat().getId(),
                        chatReaction.getUser().getId(),
                        chatReaction.getChatReactionType(),
                        true
                )
        );

        return chatReaction.getId();
    }

    @Transactional
    public void removeChatReaction(Long chatId,
                                Long userId,
                                ChatReactionType chatReactionType
    ) {
        final var user = this.userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));
        final var chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CHAT));

        this.chatRoomParticipantRepository.findByUserAndChatRoom(
                user,
                chat.getChatRoom()
        ).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_CHAT_ROOM_PARTICIPANT)
        );

        final var chatReaction = this.chatReactionRepository.findByChatIdAndUserIdAndChatReactionType(
                chatId,
                userId,
                chatReactionType
        ).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CHAT));

        this.sendChatProducer.modifyReaction(
                new ModifyReactionChatEventDto(
                        chat.getId(),
                        chatReaction.getChat().getId(),
                        chatReaction.getUser().getId(),
                        chatReaction.getChatReactionType(),
                        false
                )
        );

        this.chatReactionRepository.deleteByChatIdAndUserIdAndChatReactionType(
                chatId,
                userId,
                chatReactionType
        );
    }
}
