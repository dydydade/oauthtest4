package login.tikichat.domain.bookmark.repository;

import login.tikichat.domain.bookmark.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, CustomBookmarkRepository {

}
