package login.tikichat.domain.chatroom.service;

import login.tikichat.domain.category.dto.FindCategoryDto;
import login.tikichat.domain.category.repository.CategoryRepository;
import login.tikichat.domain.chatroom.dto.CreateChatRoomDto;
import login.tikichat.domain.chatroom.dto.FindChatRoomDto;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom.repository.ChatRoomRepository;
import login.tikichat.domain.chatroom_participant.service.ChatRoomParticipantService;
import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final CategoryRepository categoryRepository;
    private final ChatRoomParticipantService chatRoomParticipantService;

    @Transactional
    public Long createChatRoom(
            Long rootManagerUserId,
            CreateChatRoomDto.CreateChatRoomReq createChatRoomReq
    ) {
        final var category = this.categoryRepository.findByCode(
                createChatRoomReq.categoryCode()
        ).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));

        final var chatRoot = new ChatRoom(
                rootManagerUserId,
                createChatRoomReq.name(),
                createChatRoomReq.maxUserCount(),
                createChatRoomReq.tags(),
                category
        );

        this.chatRoomRepository.save(chatRoot);

        this.chatRoomParticipantService.joinChatRoom(chatRoot.getId(), rootManagerUserId);

        return chatRoot.getId();
    }

    public FindChatRoomDto.FindChatRoomRes findChatRooms(
            FindChatRoomDto.FindChatRoomReq findChatRoomReq,
            Long userId
    ) {
        final var chatRooms = this.chatRoomRepository.findChatRooms(findChatRoomReq, userId);

        return new FindChatRoomDto.FindChatRoomRes(
                chatRooms.stream().map((chatRoom ->
                        new FindChatRoomDto.FindChatRoomItemRes(
                                chatRoom.getName(),
                                chatRoom.getMaxUserCount(),
                                chatRoom.getCurrentUserCount(),
                                chatRoom.getTags(),
                                chatRoom.getRoomManagerUserId(),
                                new FindCategoryDto.FindCategoryItemRes(
                                        chatRoom.getCategory().getCode(),
                                        chatRoom.getCategory().getName(),
                                        chatRoom.getCategory().getOrderNum()
                                )
                        )
                )).toList()
        );
    }

    public FindChatRoomDto.FindChatRoomRes findChatRoomsByPopularity(
            FindChatRoomDto.FindChatRoomByPopularityReq findChatRoomReq,
            Long userId
    ) {
        // TODO: 추후 채팅방 인기순으로 상위 N개 조회하여 반환하도록 수정 필요
        final var chatRooms = this.chatRoomRepository.findAll();

        return new FindChatRoomDto.FindChatRoomRes(
                chatRooms.stream().map((chatRoom ->
                        new FindChatRoomDto.FindChatRoomItemRes(
                                chatRoom.getName(),
                                chatRoom.getMaxUserCount(),
                                chatRoom.getCurrentUserCount(),
                                chatRoom.getTags(),
                                chatRoom.getRoomManagerUserId(),
                                new FindCategoryDto.FindCategoryItemRes(
                                        chatRoom.getCategory().getCode(),
                                        chatRoom.getCategory().getName(),
                                        chatRoom.getCategory().getOrderNum()
                                )
                        )
                )).toList()
        );
    }
}
