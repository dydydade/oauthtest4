package login.tikichat.domain.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import login.tikichat.domain.category.dto.FindCategoryDto;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public class FindChatRoomDto {
    @Getter
    public enum FindChatRoomSearchKeywordColumns {
        NAME("NAME");

        private final String columnName;

        FindChatRoomSearchKeywordColumns(String columnName) {
            this.columnName = columnName;
        }
    }

    public record FindChatRoomReq(
            @NotEmpty
            @Schema(description = "채팅방 검색 키워드", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 200, minLength = 1)
            @Length(max = 200, min = 1)
            @Nullable
            String searchKeyword,
            @Schema(
                    description = "채팅방 검색 키워드로 검색할 항목들",
                    requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                    defaultValue = "[NAME]"
            )
            List<FindChatRoomSearchKeywordColumns> searchKeywordColumns,
            @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false", description = "현재 참가한 채팅방만 가져오기")
            Boolean isFetchOnlyParticipatedRoom
    ) {
        public FindChatRoomReq() {
            this(
                    null,
                    List.of(
                        FindChatRoomSearchKeywordColumns.NAME
                    ),
                    false);
        }
    }

    public record FindChatRoomItemRes(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String name,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Integer maxUserCount,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Integer currentUserCount,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<String> tags,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Long hostId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            FindCategoryDto.FindCategoryItemRes category
            // TODO: orderNum 추가해도 될지 검토
    ) {

    }

    public record FindChatRoomRes(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<FindChatRoomItemRes> chatRooms
    ) {
    }

    public record FindChatRoomByPopularityReq(
            @NotNull(message = "인기순으로 조회할 채팅방 갯수는 필수 입력값입니다.")
            @Schema(description = "인기순으로 조회할 채팅방 갯수", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 200, minLength = 1)
            @Min(value = 1, message = "인기순으로 조회할 채팅방은 최소 1개 이상이어야 합니다.")
            @Max(value = 25, message = "인기순으로 조회할 채팅방은 최대 25개 이하여야 합니다.")
            Integer popularityRank,

            @Schema(description = "인기순으로 조회할 카테고리", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            String categoryCode,

            @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false", description = "현재 팔로잉한 카테고리의 채팅방만 가져오기")
            Boolean isFetchOnlyFollowedCategoriesRooms
    ) {
        public static final int HOME_PAGE_DEFAULT_POPULARITY_RANK = 25;

        public FindChatRoomByPopularityReq() {
            this(
                    HOME_PAGE_DEFAULT_POPULARITY_RANK,
                    null,
                    false);
        }
    }
}
