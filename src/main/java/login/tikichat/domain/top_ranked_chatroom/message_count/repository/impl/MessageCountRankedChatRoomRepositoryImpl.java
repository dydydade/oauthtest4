package login.tikichat.domain.top_ranked_chatroom.message_count.repository.impl;

import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.top_ranked_chatroom.message_count.model.MessageCountRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.message_count.model.QMessageCountRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.message_count.repository.CustomMessageCountRankedChatRoomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Repository
public class MessageCountRankedChatRoomRepositoryImpl extends QuerydslRepositorySupport implements CustomMessageCountRankedChatRoomRepository {
    public MessageCountRankedChatRoomRepositoryImpl() {
        super(ChatRoom.class);
    }

    @Override
    public List<MessageCountRankedChatRoom> findMessageCountChatRooms(String categoryCode, Pageable pageable) {
        final var messageCountRankedChatRoomQ = QMessageCountRankedChatRoom.messageCountRankedChatRoom;
        final var query = super.from(messageCountRankedChatRoomQ);

        // 최대 ReportDate
        Instant maxReportDate = from(messageCountRankedChatRoomQ)
                .select(messageCountRankedChatRoomQ.reportDate.max())
                .fetchOne();

        if (maxReportDate != null) {
            maxReportDate = maxReportDate.truncatedTo(ChronoUnit.DAYS);
            query.where(messageCountRankedChatRoomQ.reportDate.goe(maxReportDate));
        }

        if (categoryCode != null && !categoryCode.isBlank()) {
            query
                    .where(messageCountRankedChatRoomQ.categoryCode.eq(categoryCode))
                    .orderBy(messageCountRankedChatRoomQ.innerCategoryRank.desc());
        } else {
            query
                    .orderBy(messageCountRankedChatRoomQ.totalRank.desc());
        }

        return query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
