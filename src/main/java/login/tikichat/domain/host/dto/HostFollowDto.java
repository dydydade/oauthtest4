package login.tikichat.domain.host.dto;


public class HostFollowDto {

    public record HostFollowRes(
            Long hostId,
            Long followerId,
            String hostNickName,
            String followerNickName
    ) {

    }
}
