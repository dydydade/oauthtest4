package login.tikichat.domain.host.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.net.URL;
import java.util.List;

@Getter
public class FindHostDto {

    @Builder
    public record FindHostItemRes(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Long hostId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            URL hostProfileImageUrl,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String hostNickname,
            // TODO: 실제 호스트 접속 정보 반환하도록 수정(추후)
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Boolean isOnline
    ) {

    }

    @Builder
    public record FindHostRes(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<FindHostItemRes> hosts
    ) {
    }
}
