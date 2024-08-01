package login.tikichat.domain.host.model;

import jakarta.persistence.*;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.user.model.User;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Builder
@Table(name = "host")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Host {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    private List<ChatRoom> chatRooms;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    private List<HostSubscription> hostSubscriptions;

    // TODO: 실제 호스트 접속 정보 반환하도록 수정(추후)
    private boolean isOnline = true;
}
