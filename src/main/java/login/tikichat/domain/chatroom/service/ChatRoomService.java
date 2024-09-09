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
import login.tikichat.domain.top_ranked_chatroom.dao.CountRankedChatRoomDao;
import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;
import login.tikichat.global.exception.chatroom.ChatRoomNotFoundException;
import lombok.RequiredArgsConstructor;
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
    private final HostRepository hostRepository;
    private final UserRepository userRepository;
    private final CountRankedChatRoomDao chatRoomDao;


    @Transactional
    public CreateChatRoomDto.CreateChatRoomRes createChatRoom(
            Long hostUserId,
            CreateChatRoomDto.CreateChatRoomReq createChatRoomReq
    ) {
        final var category = this.categoryRepository.findByCode(
                createChatRoomReq.categoryCode()
        ).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));

        User user = this.userRepository.findById(hostUserId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));

        final Host host = findHost(hostUserId, user);

        final var chatRoom = new ChatRoom(
                host,
                createChatRoomReq.name(),
                createChatRoomReq.maxUserCount(),
                user.getImageUrl(),
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

    private Host findHost(Long hostUserId, User user) {
        final var hostOptional = this.hostRepository.findByHostUserId(hostUserId);
        final Host host;

        if (hostOptional.isPresent()) {
            host = hostOptional.get();
        } else {
            host = Host.builder()
                    .user(user)
                    .isOnline(true)
                    .build();
            this.hostRepository.save(host);
        }
        return host;
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
            FindChatRoomDto.FindChatRoomByPopularityReq findChatRoomReq
    ) {
        final var start = findChatRoomReq.pageNumber() * findChatRoomReq.pageSize();
        final var end = start + findChatRoomReq.pageSize() - 1;
        final var type = "message";
        final var key = getKeyForRanking(findChatRoomReq.categoryCode(), type);
        final var chatRooms = getRankedChatRooms(key, start, end);

        return convertToFindChatRoomRes(chatRooms);
    }

    public FindChatRoomDto.FindChatRoomRes findMemberCountRankedChatRooms(
            FindChatRoomDto.FindChatRoomByPopularityReq findChatRoomReq
    ) {
        final var start = findChatRoomReq.pageNumber() * findChatRoomReq.pageSize();
        final var end = start + findChatRoomReq.pageSize() - 1;
        final var type = "member";
        final var key = getKeyForRanking(findChatRoomReq.categoryCode(), type);
        final var chatRooms = getRankedChatRooms(key, start, end);

        return convertToFindChatRoomRes(chatRooms);
    }

    private String getKeyForRanking(String categoryCode, String type) {
        if (categoryCode != null) {
            return "rank:category:" + categoryCode + ":" + type + ":chatroom";
        } else {
            return "rank:total:" + type + ":chatroom";
        }
    }

    private List<ChatRoom> getRankedChatRooms(String key, int start, int end) {
        List<String> chatRoomIds = chatRoomDao.getChatRoomRank(key, start, end);

        return chatRoomRepository.findByIdsInOrder(
                chatRoomIds.stream().map(Long::parseLong).collect(Collectors.toList())
        );
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
                            index + 1,
                            chatRoom.isRoomClosed()
                    );
                })
                .collect(Collectors.toList());

        return new FindChatRoomDto.FindChatRoomRes(chatRoomItems);
    }
}
