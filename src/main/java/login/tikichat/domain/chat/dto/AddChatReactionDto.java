package login.tikichat.domain.chat.dto;

import jakarta.validation.constraints.NotNull;
import login.tikichat.domain.chat.constants.ChatReactionType;

public class AddChatReactionDto {
    public record AddChatReactionReq(
            @NotNull
            ChatReactionType chatReactionType
    ) {
    }
}
