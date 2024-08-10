package login.tikichat.domain.host.dto;

import login.tikichat.domain.chatroom.dto.FindChatRoomDto;
import lombok.Builder;
import lombok.Getter;

import java.net.URL;

@Getter
public class HostProfileDto {

    @Builder
    public record HostProfileRes(
            URL hostProfileImageUrl,
            String hostNickname,
            String hostDescription,
            Long followerCount,
            Boolean isFollowing,
            FindChatRoomDto.FindChatRoomRes chatRooms
    ) {

    }
}
