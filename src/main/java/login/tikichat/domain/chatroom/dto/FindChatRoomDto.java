package login.tikichat.domain.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
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
            Long roomManagerUserId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            FindCategoryDto.FindCategoryItemRes category
    ) {

    }

    public record FindChatRoomRes(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<FindChatRoomItemRes> chatRooms
    ) {
    }
}
