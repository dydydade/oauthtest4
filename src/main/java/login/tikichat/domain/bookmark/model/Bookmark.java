package login.tikichat.domain.bookmark.model;

import jakarta.persistence.*;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.user.model.User;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

/**
 * 각 사용자의 채팅방 즐겨찾기 정보를 관리하는 엔티티 (User ↔ Bookmark ↔ Chatroom 관계)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bookmarks")
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chatRoom;

    @CreatedDate
    private Instant createdDate;

    public Bookmark(
            User user,
            ChatRoom chatRoom
    ) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.createdDate = Instant.now();
    }
}
