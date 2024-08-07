package login.tikichat.domain.chatroom.repository.impl;

import jakarta.persistence.OptimisticLockException;
import login.tikichat.domain.chatroom.dto.FindChatRoomDto;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom.model.QChatRoom;
import login.tikichat.domain.chatroom.repository.CustomChatRoomRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChatRoomRepositoryImpl extends QuerydslRepositorySupport implements CustomChatRoomRepository {
    public ChatRoomRepositoryImpl() {
        super(ChatRoom.class);
    }

    @Override
    public List<ChatRoom> findChatRooms(FindChatRoomDto.FindChatRoomReq findChatRoomReq, Long userId) {
        final var chatRoomQ = QChatRoom.chatRoom;
        final var query = super.from(chatRoomQ);

        if (
                findChatRoomReq.searchKeyword() != null &&
                !findChatRoomReq.searchKeywordColumns().isEmpty() &&
                !findChatRoomReq.searchKeyword().isEmpty()
        ) {
            findChatRoomReq.searchKeywordColumns().forEach((keywordColumn) -> {
                switch (keywordColumn) {
                    case NAME:
                        query.where(chatRoomQ.name.contains(
                                findChatRoomReq.searchKeyword()
                        ));
                        break;
                }
            });
        }

        return query.fetch();
    }

    @Override
    public void addCurrentUserCount(Long id) {
        final var chatRoomQ = QChatRoom.chatRoom;
        final var updateQuery = super.update(chatRoomQ);
        final var selectQuery = super.from(chatRoomQ);

        selectQuery.where(chatRoomQ.id.eq(id));
        Optional.ofNullable(selectQuery.fetchOne()).orElseThrow();

        updateQuery.where(chatRoomQ.id.eq(id));
        updateQuery.where(chatRoomQ.maxUserCount.goe(chatRoomQ.currentUserCount.add(1)));
        updateQuery.set(chatRoomQ.currentUserCount, chatRoomQ.currentUserCount.add(1));

        if (updateQuery.execute() != 1) {
            throw new OptimisticLockException(ChatRoom.class);
        }
    }

    @Override
    public void subtractCurrentUserCount(Long id) {
        final var chatRoomQ = QChatRoom.chatRoom;
        final var updateQuery = super.update(chatRoomQ);

        updateQuery.where(chatRoomQ.id.eq(id));
        updateQuery.set(chatRoomQ.currentUserCount, chatRoomQ.currentUserCount.add(-1));

        throw new OptimisticLockException(ChatRoom.class);
    }
}
