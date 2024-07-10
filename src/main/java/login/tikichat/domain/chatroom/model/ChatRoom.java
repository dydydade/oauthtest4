package login.tikichat.domain.chatroom.model;

import jakarta.persistence.*;
import login.tikichat.domain.attachment.model.Attachment;
import login.tikichat.domain.category.model.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "max_user_count", nullable = false)
    private Integer maxUserCount;

    @Column(name = "current_user_count", nullable = false)
    private Integer currentUserCount;

    @Column(name = "tags", columnDefinition = "json", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> tags;

    // 방장 userId
    @Column(name = "room_manager_user_id", nullable = false)
    private Long roomManagerUserId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_code", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Attachment> attachments = new ArrayList<>();

    public ChatRoom(
            Long roomManagerUserId,
            String name,
            Integer maxUserCount,
            List<String> tags,
            Category category
    ) {
        this.roomManagerUserId = roomManagerUserId;
        this.name = name;
        this.maxUserCount = maxUserCount;
        this.tags = tags;
        this.category = category;
        this.currentUserCount = 0;
    }

    // 연관관계 편의 메소드
    public void addAttachment(Attachment attachment) {
        if (!this.attachments.contains(attachment)) {
            this.attachments.add(attachment);
            attachment.setChatRoom(this);
        }
    }
}
