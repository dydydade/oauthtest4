package login.tikichat.domain.host.model;

import jakarta.persistence.*;
import login.tikichat.domain.category.model.Category;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.user.model.User;
import lombok.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@Table(name = "host")
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Host {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "host_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    private List<HostFollowStatus> hostFollowStatuses = new ArrayList<>();

    public URL getHostProfileImageUrl() {
        return user.getImageUrl();
    }

    public String getHostNickname() {
        return user.getNickname();
    }

    public String getHostDescription() {
        return user.getDescription();
    }

    public Host(User user,
                List<ChatRoom> chatRooms,
                List<HostFollowStatus> hostFollowStatuses
    ) {
        this.user = user;
        this.chatRooms = chatRooms;
        this.hostFollowStatuses = hostFollowStatuses;
    }

    // 연관관계 편의 메소드
    public void addChatRoom(ChatRoom chatRoom) {
        // 현재 Host의 chatRooms 리스트에 추가하고, ChatRoom의 Host를 현재 Host로 설정
        if (!this.chatRooms.contains(chatRoom)) {
            this.chatRooms.add(chatRoom);
            chatRoom.setHost(this);
        }
    }
}
