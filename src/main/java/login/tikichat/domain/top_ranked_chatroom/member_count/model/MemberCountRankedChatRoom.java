package login.tikichat.domain.top_ranked_chatroom.member_count.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCountRankedChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "chat_room_id")
    private Long chatRoomId;

    @Column(nullable = false, name = "chat_room_name")
    private String chatRoomName;

    @Column(nullable = false, name = "member_count")
    private Integer memberCount;

    @Column(name = "total_rank")
    private Integer totalRank;

    @Column(nullable = false, name = "category_code")
    private String categoryCode;

    @Column(nullable = false, name = "inner_category_rank")
    private Integer innerCategoryRank;

    @Column(nullable = false, name = "report_date")
    private final Instant reportDate = Instant.now();

    public MemberCountRankedChatRoom(Long chatRoomId, String chatRoomName, Integer memberCount, Integer totalRank, String categoryCode, Integer innerCategoryRank) {
        this.chatRoomId = chatRoomId;
        this.chatRoomName = chatRoomName;
        this.memberCount = memberCount;
        this.totalRank = totalRank;
        this.categoryCode = categoryCode;
        this.innerCategoryRank = innerCategoryRank;
    }
}
