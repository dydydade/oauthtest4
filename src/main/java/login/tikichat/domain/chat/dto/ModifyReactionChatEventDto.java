package login.tikichat.domain.chat.dto;

import login.tikichat.domain.chat.constants.ChatReactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ModifyReactionChatEventDto {
    private Long id;
    private Long chatId;
    private Long senderUserId;
    private ChatReactionType chatReactionType;
    private Boolean isReaction;
}
