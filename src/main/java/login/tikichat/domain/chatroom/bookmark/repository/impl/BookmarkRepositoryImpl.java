package login.tikichat.domain.chatroom.bookmark.repository.impl;

import login.tikichat.domain.chatroom.bookmark.model.Bookmark;
import login.tikichat.domain.chatroom.bookmark.model.QBookmark;
import login.tikichat.domain.chatroom.bookmark.repository.CustomBookmarkRepository;
import login.tikichat.domain.chatroom.model.QChatRoom;
import login.tikichat.domain.user.model.QUser;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BookmarkRepositoryImpl extends QuerydslRepositorySupport implements CustomBookmarkRepository {
    public BookmarkRepositoryImpl() {
        super(Bookmark.class);
    }

    @Override
    public boolean existsByUserIdAndChatroomId(Long userId, Long chatroomId) {
        final var bookmarkQ = QBookmark.bookmark;
        final var userQ = QUser.user;
        final var chatroomQ = QChatRoom.chatRoom;

        final var query = super.from(bookmarkQ);

        return query
                .join(bookmarkQ.user, userQ).fetchJoin()
                .join(bookmarkQ.chatRoom, chatroomQ).fetchJoin()
                .where(userQ.id.eq(userId))
                .where(chatroomQ.id.eq(chatroomId))
                .fetchCount() > 0;
    }

    @Override
    public Optional<Bookmark> findByUserIdAndChatroomId(Long userId, Long chatroomId) {
        final var bookmarkQ = QBookmark.bookmark;
        final var userQ = QUser.user;
        final var chatroomQ = QChatRoom.chatRoom;

        final var query = super.from(bookmarkQ);

        Bookmark bookmark = query
                .join(bookmarkQ.user, userQ)
                .join(bookmarkQ.chatRoom, chatroomQ)
                .where(userQ.id.eq(userId))
                .where(chatroomQ.id.eq(chatroomId))
                .fetchFirst();

        return Optional.ofNullable(bookmark);
    }
}
