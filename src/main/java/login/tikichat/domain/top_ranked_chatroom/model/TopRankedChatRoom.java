package login.tikichat.domain.top_ranked_chatroom.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopRankedChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "chat_room_id")
    private Long chatRoomId;

    @Column(nullable = false, name = "message_count")
    private Integer messageCount;

    @Column(name = "rank")
    private Integer rank;

    @Column(nullable = false, name = "report_date")
    private final Instant reportDate = Instant.now();

    public TopRankedChatRoom(Long chatRoomId, Integer messageCount, Integer rank) {
        this.chatRoomId = chatRoomId;
        this.messageCount = messageCount;
        this.rank = rank;
    }
}
