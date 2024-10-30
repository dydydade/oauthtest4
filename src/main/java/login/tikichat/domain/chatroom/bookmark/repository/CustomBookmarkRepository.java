package login.tikichat.domain.chatroom.bookmark.repository;

import login.tikichat.domain.chatroom.bookmark.model.Bookmark;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomBookmarkRepository {
    boolean existsByUserIdAndChatroomId(Long userId, Long chatroomId);
    Optional<Bookmark> findByUserIdAndChatroomId(Long userId, Long chatroomId);
}
