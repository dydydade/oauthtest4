package login.tikichat.domain.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public class CreateChatRoomDto {
    public record CreateChatRoomReq(
            @NotEmpty
            @Length(min = 3, max = 200)
            @Schema(description = "채팅방 이름", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 200, minLength = 3)
            String name,

            @Min(2)
            @Max(500)
            @Schema(description = "채팅방 이름", requiredMode = Schema.RequiredMode.REQUIRED)
            Integer maxUserCount,

            @NotEmpty
            @Schema(description = "카테고리 코드", requiredMode = Schema.RequiredMode.REQUIRED)
            String categoryCode,

            @Size(min = 0, max = 100)
            @NotEmpty
            @Schema(description = "채팅방 태그", requiredMode = Schema.RequiredMode.REQUIRED)
            List<String> tags
    ) {

    }

    public record CreateChatRoomRes(
        Long id
    ) {

    }
}
