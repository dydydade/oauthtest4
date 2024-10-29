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
            @Schema(description = "호스트 검색 키워드(키워드와 닉네임이 매칭되는 호스트만 조회)", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 200, minLength = 1)
            @Length(max = 200, min = 1)
            @Nullable
            String searchKeyword,

            @Schema(description = "특정 팔로워가 팔로우하는 호스트 정보만 조회", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            Long followerId
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
