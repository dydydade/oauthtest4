package login.tikichat.domain.host.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.net.URL;
import java.util.List;

@Getter
public class FindFollowerDto {

    @Builder
    public record FindFollowerItemRes(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Long followerId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            URL followerProfileImageUrl,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String followerNickname
    ) {

    }

    @Builder
    public record FindFollowerRes(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<FindFollowerItemRes> hosts
    ) {
    }
}
