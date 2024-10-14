package login.tikichat.domain.bookmark.repository;

import login.tikichat.domain.bookmark.model.Bookmark;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomBookmarkRepository {
    boolean existsByUserIdAndChatroomId(Long userId, Long chatroomId);
    Optional<Bookmark> findByUserIdAndChatroomId(Long userId, Long chatroomId);
}
