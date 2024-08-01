package login.tikichat.domain.host.model;

import jakarta.persistence.*;
import login.tikichat.domain.follower.model.Follower;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "host_subscription")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HostSubscription {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "host_follower_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Follower follower;

    @Column(name = "subscribe_date", nullable = false)
    private Instant subscribeDate;
}
