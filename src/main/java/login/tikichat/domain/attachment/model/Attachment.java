package login.tikichat.domain.attachment.model;

import jakarta.persistence.*;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.user.model.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4000, nullable = false)
    private String path;

    @Column(length = 1000, nullable = false)
    private String filename;

    @Column(length = 1000, nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String ext;

    @ManyToOne()
    @JoinColumn(name = "uploader_user_id", nullable = false)
    private User uploaderUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    public Attachment(
            User uploaderUser,
            ChatRoom chatRoom,
            String path,
            String filename,
            String originalFilename,
            String ext
    ) {
        this.filename = filename;
        this.uploaderUser = uploaderUser;
        this.chatRoom = chatRoom;
        this.path = path;
        this.originalFilename = originalFilename;
        this.ext = ext;
    }

    // ChatRoom 과의 연관관계 설정 편의 메서드
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;

        if (chatRoom != null && !chatRoom.getAttachments().contains(this)) {
            chatRoom.getAttachments().add(this);
        }
    }
}
