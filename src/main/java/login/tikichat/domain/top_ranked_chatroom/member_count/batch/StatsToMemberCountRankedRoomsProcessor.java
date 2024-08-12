package login.tikichat.domain.top_ranked_chatroom.member_count.batch;

import login.tikichat.domain.top_ranked_chatroom.member_count.dto.ChatRoomMemberCountStatsDto;
import login.tikichat.domain.top_ranked_chatroom.member_count.model.MemberCountRankedChatRoom;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;


@StepScope
public class StatsToMemberCountRankedRoomsProcessor implements ItemProcessor<ChatRoomMemberCountStatsDto, MemberCountRankedChatRoom> {

    @Override
    public MemberCountRankedChatRoom process(ChatRoomMemberCountStatsDto stats) {
        return new MemberCountRankedChatRoom(
                stats.getChatRoomId(),
                stats.getChatRoomName(),
                stats.getMemberCount(),
                stats.getRank(),
                stats.getCategoryCode(),
                stats.getInnerCategoryRank()
        );
    }
}
