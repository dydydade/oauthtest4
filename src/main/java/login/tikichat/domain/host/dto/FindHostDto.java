package login.tikichat.domain.host.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.net.URL;
import java.util.List;

@Getter
public class FindHostDto {

    @Builder
    public record FindHostReq(
            @Schema(description = "호스트 검색 키워드", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 200, minLength = 1)
            @Length(max = 200, min = 1)
            @Nullable
            String searchKeyword
    ) {

    }

    @Builder
    public record FindHostItemRes(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Long hostId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            URL hostProfileImageUrl,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String hostNickname,
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
