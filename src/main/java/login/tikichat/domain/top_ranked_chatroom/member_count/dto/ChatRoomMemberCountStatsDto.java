package login.tikichat.domain.top_ranked_chatroom.member_count.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomMemberCountStatsDto implements Serializable {
    private Long chatRoomId;
    private String chatRoomName;
    private String categoryCode;
    private Integer memberCount;
    private Integer rank;
    private Integer innerCategoryRank;

    public void incrementCount(int count) {
        this.memberCount += count;
    }
}
