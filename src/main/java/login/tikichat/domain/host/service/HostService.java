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
import login.tikichat.domain.user.service.UserStatusService;
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
public class HostService {

    private final HostFollowStatusRepository hostFollowStatusRepository;
    private final HostRepository hostRepository;
    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final UserStatusService userStatusService;

    @Transactional
    public HostFollowDto.HostFollowRes follow(Long hostId, Long userId) {
        final var host = hostRepository.findById(hostId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_HOST));
        final var follower = followerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = this.userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER));
                    Follower newFollower = Follower.builder()
                            .user(user)
                            .build();
                    followerRepository.save(newFollower);
                    return newFollower;
                });

        if (hostFollowStatusRepository.findByHostIdAndFollowerId(hostId, follower.getId()).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_FOLLOWING_HOST);
        }

        final var hostSubscription = HostFollowStatus.builder()
                .host(host)
                .follower(follower)
                .followDate(Instant.now())
                .build();

        hostFollowStatusRepository.save(hostSubscription);

        return new HostFollowDto.HostFollowRes(host.getUser().getId(), follower.getUser().getId(), host.getUser().getNickname(), follower.getUser().getNickname());
    }

    @Transactional
    public HostFollowDto.HostFollowRes unfollow(Long hostId, Long userId) {
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
                .chatRooms(this.toResponse(host.getChatRooms()))
                .build();
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

    public FindChatRoomDto.FindChatRoomRes toResponse(List<ChatRoom> chatRooms) {
        List<FindChatRoomDto.FindChatRoomItemRes> chatRoomResponses = IntStream.range(0, chatRooms.size())
                .mapToObj(index -> {
                    ChatRoom chatRoom = chatRooms.get(index);
                    Boolean hostOnlineStatus = userStatusService.getUserStatus(chatRoom.getHost().getUser().getId()).orElse(false);

                    return FindChatRoomDto.FindChatRoomItemRes.builder()
                            .name(chatRoom.getName())
                            .maxUserCount(chatRoom.getMaxUserCount())
                            .currentUserCount(chatRoom.getCurrentUserCount())
                            .tags(chatRoom.getTags())
                            .chatRoomImageUrl(chatRoom.getImageUrl())
                            .category(new FindCategoryDto.FindCategoryItemRes(
                                    chatRoom.getCategory().getCode(),
                                    chatRoom.getCategory().getName(),
                                    chatRoom.getCategory().getOrderNum()
                            ))
                            .orderNum(index)
                            .isRoomClosed(chatRoom.isRoomClosed())
                            .isHostOnline(hostOnlineStatus)
                            .isBookmarked(null)
                            .unreadChatCount(null)
                            .lastChatTime(null)
                            .build();
                })
                .collect(Collectors.toList());

        return new FindChatRoomDto.FindChatRoomRes(chatRoomResponses);
    }

    @Transactional(readOnly = true)
    public FindHostDto.FindHostRes findHosts(FindHostDto.FindHostReq findHostReq) {
        List<FindHostDto.FindHostItemRes> hostItems = hostRepository.findHosts(findHostReq).stream()
                .map(host -> FindHostDto.FindHostItemRes.builder()
                        .hostId(host.getId())
                        .hostNickname(host.getHostNickname())
                        .hostProfileImageUrl(host.getHostProfileImageUrl())
                        .isOnline(userStatusService.getUserStatus(host.getUser().getId()).orElse(false))
                        .build())
                .toList();

        return new FindHostDto.FindHostRes(hostItems);
    }

    @Transactional(readOnly = true)
    public FindHostDto.FindHostRes findMyFollowedHosts(Long followerUserId) {
        List<FindHostDto.FindHostItemRes> hostItems = hostFollowStatusRepository.findByFollowerUserId(followerUserId).stream()
                .map(HostFollowStatus::getHost)
                .map(host -> FindHostDto.FindHostItemRes.builder()
                        .hostId(host.getId())
                        .hostNickname(host.getHostNickname())
                        .hostProfileImageUrl(host.getHostProfileImageUrl())
                        .isOnline(userStatusService.getUserStatus(host.getUser().getId()).orElse(false))
                        .build())
                .toList();

        return new FindHostDto.FindHostRes(hostItems);
    }
}
