package login.tikichat.domain.top_ranked_chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomStatsDto implements Serializable {
    private Long chatRoomId;
    private Integer messageCount;
    private Integer rank;

    public void incrementCount(int count) {
        this.messageCount += count;
    }
}
