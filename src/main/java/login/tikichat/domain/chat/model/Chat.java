package login.tikichat.domain.chat.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import login.tikichat.domain.chatroom.model.ChatRoom;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chats")
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, name = "sender_user_id")
    private Long senderUserId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @CreatedDate
    private Instant createdDate;

    @OneToMany(mappedBy = "chat")
    private Set<ChatReaction> chatReactions = new HashSet<>();

    public static Chat sendMessage(
            Long senderUserId,
            ChatRoom chatRoom,
            String content
    ) {
        return Chat
                .builder()
                .chatRoom(chatRoom)
                .content(content)
                .senderUserId(senderUserId)
                .build();
    }
}
