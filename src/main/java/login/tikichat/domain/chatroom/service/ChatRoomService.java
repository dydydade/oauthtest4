package login.tikichat.domain.chatroom.service;

import login.tikichat.domain.attachment.model.Attachment;
import login.tikichat.domain.category.dto.FindCategoryDto;
import login.tikichat.domain.category.repository.CategoryRepository;
import login.tikichat.domain.chatroom.dto.CreateChatRoomDto;
import login.tikichat.domain.chatroom.dto.FindChatRoomDto;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom.repository.ChatRoomRepository;
import login.tikichat.domain.chatroom_participant.service.ChatRoomParticipantService;
import login.tikichat.domain.host.model.Host;
import login.tikichat.domain.host.repository.HostRepository;
import login.tikichat.domain.top_ranked_chatroom.member_count.model.MemberCountRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.member_count.repository.MemberCountRankedChatRoomRepository;
import login.tikichat.domain.top_ranked_chatroom.message_count.model.MessageCountRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.message_count.repository.MessageCountRankedChatRoomRepository;
import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;
import login.tikichat.global.exception.chatroom.ChatRoomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final CategoryRepository categoryRepository;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final MessageCountRankedChatRoomRepository messageCountRankedChatRoomRepository;
    private final MemberCountRankedChatRoomRepository memberCountRankedChatRoomRepository;
    private final HostRepository hostRepository;
    private final UserRepository userRepository;


    @Transactional
    public CreateChatRoomDto.CreateChatRoomRes createChatRoom(
            Long hostId,
            CreateChatRoomDto.CreateChatRoomReq createChatRoomReq
    ) {
        final var category = this.categoryRepository.findByCode(
                createChatRoomReq.categoryCode()
        ).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));

        User user = this.userRepository.findById(hostId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        final var host = this.hostRepository.findById(hostId)
                .orElse(Host.builder()
                        .user(user)
                        .isOnline(true)
                        .build());

        this.hostRepository.save(host);

        final var chatRoom = new ChatRoom(
                host,
                createChatRoomReq.name(),
                createChatRoomReq.maxUserCount(),
                createChatRoomReq.tags(),
                category
        );

        chatRoom.setHost(host);

        this.chatRoomRepository.save(chatRoom);

        this.chatRoomParticipantService.joinChatRoom(chatRoom.getId(), host.getUser().getId());

        return new CreateChatRoomDto.CreateChatRoomRes(
                chatRoom.getId(),
                host.getId()
        );
    }

    public FindChatRoomDto.FindChatRoomRes findChatRooms(
            FindChatRoomDto.FindChatRoomReq findChatRoomReq,
            Long userId
    ) {
        final var chatRooms = this.chatRoomRepository.findChatRooms(findChatRoomReq, userId);

        return convertToFindChatRoomRes(chatRooms);
    }

    public ChatRoom findById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
    }

    @Transactional
    public void linkAttachment(Long chatRoomId, Attachment attachment) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        chatRoom.addAttachment(attachment);
    }


    public FindChatRoomDto.FindChatRoomRes findMessageCountRankedChatRooms(
            FindChatRoomDto.FindChatRoomByPopularityReq findChatRoomReq,
            Long userId
    ) {
        final var chatRooms = this.findMessageCountChatRooms(findChatRoomReq).stream()
                .map(rankedChatRoom -> chatRoomRepository
                        .findById(rankedChatRoom.getChatRoomId())
                        .orElseThrow(ChatRoomNotFoundException::new))
                .toList();

        return convertToFindChatRoomRes(chatRooms);
    }

    private List<MessageCountRankedChatRoom> findMessageCountChatRooms(FindChatRoomDto.FindChatRoomByPopularityReq request) {
        Pageable limit = PageRequest.of(0, request.popularityRank());
        return messageCountRankedChatRoomRepository.findMessageCountChatRooms(request.categoryCode(), limit);
    }


    public FindChatRoomDto.FindChatRoomRes findMemberCountRankedChatRooms(
            FindChatRoomDto.FindChatRoomByPopularityReq findChatRoomReq,
            Long userId
    ) {
        final var chatRooms = this.findMemberCountChatRooms(findChatRoomReq).stream()
                .map(rankedChatRoom -> chatRoomRepository
                        .findById(rankedChatRoom.getChatRoomId())
                        .orElseThrow(ChatRoomNotFoundException::new))
                .toList();

        return convertToFindChatRoomRes(chatRooms);
    }

    private List<MemberCountRankedChatRoom> findMemberCountChatRooms(FindChatRoomDto.FindChatRoomByPopularityReq request) {
        Pageable limit = PageRequest.of(0, request.popularityRank());
        return memberCountRankedChatRoomRepository.findMemberCountChatRooms(request.categoryCode(), limit);
    }

    private FindChatRoomDto.FindChatRoomRes convertToFindChatRoomRes(List<ChatRoom> chatRooms) {
        List<FindChatRoomDto.FindChatRoomItemRes> chatRoomItems = IntStream.range(0, chatRooms.size())
                .mapToObj(index -> {
                    ChatRoom chatRoom = chatRooms.get(index);
                    return new FindChatRoomDto.FindChatRoomItemRes(
                            chatRoom.getName(),
                            chatRoom.getMaxUserCount(),
                            chatRoom.getCurrentUserCount(),
                            chatRoom.getTags(),
                            chatRoom.getHost().getUser().getId(),
                            new FindCategoryDto.FindCategoryItemRes(
                                    chatRoom.getCategory().getCode(),
                                    chatRoom.getCategory().getName(),
                                    chatRoom.getCategory().getOrderNum()
                            ),
                            index + 1
                    );
                })
                .collect(Collectors.toList());

        return new FindChatRoomDto.FindChatRoomRes(chatRoomItems);
    }
}
