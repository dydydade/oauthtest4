package login.tikichat.domain.chatroom.repository.impl;

import jakarta.persistence.OptimisticLockException;
import login.tikichat.domain.chatroom.dto.FindChatRoomDto;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom.model.QChatRoom;
import login.tikichat.domain.chatroom.repository.CustomChatRoomRepository;
import login.tikichat.domain.host.model.QHost;
import login.tikichat.domain.user.model.QUser;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class ChatRoomRepositoryImpl extends QuerydslRepositorySupport implements CustomChatRoomRepository {
    public ChatRoomRepositoryImpl() {
        super(ChatRoom.class);
    }

    @Override
    public List<ChatRoom> findChatRooms(FindChatRoomDto.FindChatRoomReq findChatRoomReq, Long userId) {
        final var chatRoomQ = QChatRoom.chatRoom;
        final var hostQ = QHost.host;
        final var userQ = QUser.user;
        final var query = super.from(chatRoomQ);

        if (
                findChatRoomReq.searchKeyword() != null &&
                !findChatRoomReq.searchKeywordColumns().isEmpty() &&
                !findChatRoomReq.searchKeyword().isEmpty()
        ) {
            findChatRoomReq.searchKeywordColumns().forEach((keywordColumn) -> {
                switch (keywordColumn) {
                    case NAME:
                        query
                                .where(chatRoomQ.name.contains(
                                findChatRoomReq.searchKeyword()
                        ));
                        break;
                }
            });
        }

        query
            .join(chatRoomQ.host, hostQ).fetchJoin()
            .join(hostQ.user, userQ).fetchJoin();

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

    @Override
    public List<ChatRoom> findByIdsInOrder(List<Long> chatRoomIds) {
        final var chatRoomQ = QChatRoom.chatRoom;
        final var hostQ = QHost.host;
        final var userQ = QUser.user;
        final var selectQuery = super.from(chatRoomQ);

        List<ChatRoom> chatRooms = selectQuery.where(chatRoomQ.id.in(chatRoomIds))
                .join(chatRoomQ.host, hostQ).fetchJoin()
                .join(hostQ.user, userQ).fetchJoin()
                .fetch();

        Map<Long, ChatRoom> chatRoomMap = chatRooms.stream()
                .collect(Collectors.toMap(ChatRoom::getId, Function.identity()));

        return chatRoomIds.stream()
                .map(chatRoomMap::get)
                .collect(Collectors.toList());
    }
}
