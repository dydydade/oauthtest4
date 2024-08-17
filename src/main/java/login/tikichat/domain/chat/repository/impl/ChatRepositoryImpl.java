package login.tikichat.domain.chat.repository.impl;

import login.tikichat.domain.category.model.QCategory;
import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.chat.model.QChat;
import login.tikichat.domain.chat.repository.CustomChatRepository;
import login.tikichat.domain.chatroom.model.QChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public class ChatRepositoryImpl extends QuerydslRepositorySupport implements CustomChatRepository {
    public ChatRepositoryImpl() {
        super(Chat.class);
    }

    @Override
    public List<Chat> findChats(Long chatRoomId, Integer take, Long nextCursor) {
        final var chatQ = QChat.chat;
        final var query = super.from(chatQ);

        query.where(chatQ.chatRoom.id.eq(chatRoomId));
        query.limit(take);
        query.orderBy(chatQ.id.desc());

        if (nextCursor != null) {
            query.where(chatQ.id.lt(nextCursor));
        }

        return query.fetch();
    }

    @Override
    public Page<Chat> findAllByCreatedDateBetween(Instant startDate, Instant endDate, Pageable pageable) {
        final var chatQ = QChat.chat;
        final var chatRoomQ = QChatRoom.chatRoom;
        final var categoryQ = QCategory.category;
        final var query = super.from(chatQ);

        query.leftJoin(chatQ.chatRoom, chatRoomQ).fetchJoin()
                .leftJoin(chatRoomQ.category, categoryQ).fetchJoin();

        query.where(
                chatQ.createdDate.between(startDate, endDate)
        );

        final List<Chat> chats = getQuerydsl().applyPagination(pageable, query).fetch();
        final long total = query.fetchCount();

        return new PageImpl<>(chats, pageable, total);
    }
}
