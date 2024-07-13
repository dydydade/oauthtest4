package login.tikichat.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendChatEventDto {
    private Long id;
    private String content;
    private Long senderUserId;
    private Long chatRoomId;
    private Instant createdDate;
}
