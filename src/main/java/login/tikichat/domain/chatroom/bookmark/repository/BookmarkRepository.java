package login.tikichat.domain.chatroom.bookmark.repository;

import login.tikichat.domain.chatroom.bookmark.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, CustomBookmarkRepository {

}
