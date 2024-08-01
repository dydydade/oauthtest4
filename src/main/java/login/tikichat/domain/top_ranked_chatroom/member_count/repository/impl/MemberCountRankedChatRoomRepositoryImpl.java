package login.tikichat.domain.top_ranked_chatroom.member_count.repository.impl;

import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.top_ranked_chatroom.member_count.model.MemberCountRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.member_count.model.QMemberCountRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.member_count.repository.CustomMemberCountRankedChatRoomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Repository
public class MemberCountRankedChatRoomRepositoryImpl extends QuerydslRepositorySupport implements CustomMemberCountRankedChatRoomRepository {
    public MemberCountRankedChatRoomRepositoryImpl() {
        super(ChatRoom.class);
    }

    @Override
    public List<MemberCountRankedChatRoom> findMemberCountChatRooms(String categoryCode, Pageable pageable) {
        final var memberCountRankedChatRoomQ = QMemberCountRankedChatRoom.memberCountRankedChatRoom;
        final var query = super.from(memberCountRankedChatRoomQ);

        // 최대 ReportDate
        Instant maxReportDate = from(memberCountRankedChatRoomQ)
                .select(memberCountRankedChatRoomQ.reportDate.max())
                .fetchOne();

        if (maxReportDate != null) {
            maxReportDate = maxReportDate.truncatedTo(ChronoUnit.DAYS);
            query.where(memberCountRankedChatRoomQ.reportDate.goe(maxReportDate));
        }

        if (categoryCode != null && !categoryCode.isBlank()) {
            query
                .where(memberCountRankedChatRoomQ.categoryCode.eq(categoryCode))
                .orderBy(memberCountRankedChatRoomQ.innerCategoryRank.desc());
        } else {
            query
                .orderBy(memberCountRankedChatRoomQ.totalRank.desc());
        }

        return query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
