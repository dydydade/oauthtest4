package login.tikichat.domain.follower.model;

import jakarta.persistence.*;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.host.model.HostSubscription;
import login.tikichat.domain.user.model.User;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "follower")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Follower {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    private List<HostSubscription> hostSubscriptions;
}
