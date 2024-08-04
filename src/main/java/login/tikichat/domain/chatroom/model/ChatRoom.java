package login.tikichat.domain.chatroom.model;

import jakarta.persistence.*;
import login.tikichat.domain.attachment.model.Attachment;
import login.tikichat.domain.category.model.Category;
import login.tikichat.domain.host.model.Host;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
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

    // Host: 채팅방 개설자
    @ManyToOne(optional = false)
    @JoinColumn(name = "host", nullable = false)
    private Host host;


    @ManyToOne(optional = false)
    @JoinColumn(name = "category_code", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Attachment> attachments = new ArrayList<>();

    // TODO: 채팅방 종료 상태 필드 추가 필요 (호스트 페이지에 종료된 채팅방 별도 표시 목적)

    public ChatRoom(
            Host host,
            String name,
            Integer maxUserCount,
            List<String> tags,
            Category category
    ) {
        this.host = host;
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


    // Host와의 연관관계 설정 편의 메서드
    public void setHost(Host host) {
        // 새로운 User 설정
        this.host = host;

        // 새로운 Host 의 ChatRoom 리스트에 현재 ChatRoom 이 없다면 추가
        if (host != null && !host.getChatRooms().contains(this)) {
            host.getChatRooms().add(this);
        }
    }
}
