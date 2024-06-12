package login.tikichat.domain.chatroom.repository.impl;

import login.tikichat.domain.chatroom.dto.FindChatRoomDto;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom.model.QChatRoom;
import login.tikichat.domain.chatroom.repository.CustomChatRoomRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

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
}
