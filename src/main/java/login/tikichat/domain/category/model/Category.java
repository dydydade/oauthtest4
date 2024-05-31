package login.tikichat.domain.category.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import login.tikichat.domain.chatroom.model.ChatRoom;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatroom_categories")
public class Category {
    @Id
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer orderNum;

    @OneToMany(mappedBy = "category")
    Set<ChatRoom> chatRooms = new HashSet<>();

    public Category(String code, String name, Integer orderNum) {
        this.code = code;
        this.name = name;
        this.orderNum = orderNum;
    }
}
