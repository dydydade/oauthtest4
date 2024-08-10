package login.tikichat.domain.host.service;

import login.tikichat.domain.category.dto.FindCategoryDto;
import login.tikichat.domain.chatroom.dto.FindChatRoomDto;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.host.dto.FindFollowerDto;
import login.tikichat.domain.host.model.Follower;
import login.tikichat.domain.host.repository.FollowerRepository;
import login.tikichat.domain.host.dto.FindHostDto;
import login.tikichat.domain.host.dto.HostProfileDto;
import login.tikichat.domain.host.dto.HostFollowDto;
import login.tikichat.domain.host.model.HostFollowStatus;
import login.tikichat.domain.host.repository.HostRepository;
import login.tikichat.domain.host.repository.HostFollowStatusRepository;
import login.tikichat.domain.user.model.User;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.exception.BusinessException;
import login.tikichat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class HostFollowStatusService {

    private final HostFollowStatusRepository hostFollowStatusRepository;
    private final HostRepository hostRepository;
    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;

    @Transactional
    public HostFollowDto.HostFollowRes subscribe(Long hostId, Long userId) {
        final var host = hostRepository.findById(hostId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_HOST));
        // TODO: 아래 코드는 특정 호스트를 구독하는 시점에 신규 Follower 를 생성 / 회원가입 시점에 Follower 를 생성할지 고민 필요
        final var follower = followerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = this.userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));
                    Follower newFollower = Follower.builder()
                            .user(user)
                            .build();
                    followerRepository.save(newFollower);
                    return newFollower;
                });

        final var hostSubscription = HostFollowStatus.builder()
                .host(host)
                .follower(follower)
                .followDate(Instant.now())
                .build();

        hostFollowStatusRepository.save(hostSubscription);

        return new HostFollowDto.HostFollowRes(host.getUser().getId(), follower.getUser().getId(), host.getUser().getNickname(), follower.getUser().getNickname());
    }

    @Transactional
    public HostFollowDto.HostFollowRes unsubscribe(Long hostId, Long userId) {
        final var host = hostRepository.findById(hostId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_HOST));
        final var follower = followerRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_FOLLOWER));

        HostFollowStatus hostFollowStatus = hostFollowStatusRepository.findByHostIdAndFollowerId(host.getId(), follower.getId()).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_HOST_FOLLOW_STATUS));

        hostFollowStatusRepository.delete(hostFollowStatus);

        return new HostFollowDto.HostFollowRes(host.getUser().getId(), follower.getUser().getId(), host.getUser().getNickname(), follower.getUser().getNickname());
    }

    @Transactional(readOnly = true)
    public HostProfileDto.HostProfileRes getHostProfile(Long hostId, Long userId) {
        final var host = hostRepository.findById(hostId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_HOST));

        return HostProfileDto.HostProfileRes.builder()
                .hostProfileImageUrl(host.getHostProfileImageUrl())
                .hostNickname(host.getHostNickname())
                .hostDescription(host.getHostDescription())
                .followerCount(this.countByHostId(hostId))
                .isFollowing(this.existsByHostIdAndFollowerUserId(host.getId(), userId))
                .chatRooms(this.convertChatRoomsToResponse(host.getChatRooms()))
                .build();
    }

    @Transactional(readOnly = true)
    public FindHostDto.FindHostRes findFollowedHosts(Long followerUserId) {
        List<FindHostDto.FindHostItemRes> hostItems = hostFollowStatusRepository.findByFollowerUserId(followerUserId).stream()
                .map(HostFollowStatus::getHost)
                .map(host -> FindHostDto.FindHostItemRes.builder()
                        .hostId(host.getId())
                        .hostNickname(host.getHostNickname())
                        .hostProfileImageUrl(host.getHostProfileImageUrl())
                        .isOnline(host.isOnline())
                        .build())
                .toList();

        return new FindHostDto.FindHostRes(hostItems);
    }

    @Transactional(readOnly = true)
    public FindFollowerDto.FindFollowerRes findTargetHostFollowers(Long hostUserId) {
        List<FindFollowerDto.FindFollowerItemRes> followerItems = hostFollowStatusRepository.findByHostUserId(hostUserId).stream()
                .map(HostFollowStatus::getFollower)
                .map(follower -> FindFollowerDto.FindFollowerItemRes.builder()
                        .followerId(follower.getId())
                        .followerNickname(follower.getUser().getNickname())
                        .followerProfileImageUrl(follower.getUser().getImageUrl())
                        .build())
                .toList();

        return new FindFollowerDto.FindFollowerRes(followerItems);
    }


    @Transactional(readOnly = true)
    public boolean existsByHostIdAndFollowerUserId(Long hostId, Long userId) {
        return hostFollowStatusRepository.existsByHostIdAndFollowerUserId(hostId, userId);
    }

    @Transactional(readOnly = true)
    public long countByHostId(Long hostId) {
        return hostFollowStatusRepository.countByHostId(hostId);
    }

    public FindChatRoomDto.FindChatRoomRes convertChatRoomsToResponse(List<ChatRoom> chatRooms) {
        List<FindChatRoomDto.FindChatRoomItemRes> chatRoomResponses = IntStream.range(0, chatRooms.size())
                .mapToObj(index -> {
                    ChatRoom chatRoom = chatRooms.get(index);
                    return FindChatRoomDto.FindChatRoomItemRes.builder()
                            .hostId(chatRoom.getHost().getUser().getId())
                            .name(chatRoom.getName())
                            .currentUserCount(chatRoom.getCurrentUserCount())
                            .category(new FindCategoryDto.FindCategoryItemRes(
                                    chatRoom.getCategory().getCode(),
                                    chatRoom.getCategory().getName(),
                                    chatRoom.getCategory().getOrderNum()
                            ))
                            .maxUserCount(chatRoom.getMaxUserCount())
                            .tags(chatRoom.getTags())
                            .orderNum(index)
                            .isRoomClosed(chatRoom.isRoomClosed())
                            .build();
                })
                .collect(Collectors.toList());

        return new FindChatRoomDto.FindChatRoomRes(chatRoomResponses);
    }
}
