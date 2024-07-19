package login.tikichat.domain.chatroom.scheduler;

import login.tikichat.domain.top_ranked_chatroom.model.TopRankedChatRoom;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;


@StepScope
public class StatsToTopRankedRoomsProcessor implements ItemProcessor<ChatRoomStatsDto, TopRankedChatRoom> {

    @Override
    public TopRankedChatRoom process(ChatRoomStatsDto stats) {
        return new TopRankedChatRoom(
                stats.getChatRoomId(),
                stats.getMessageCount(),
                stats.getRank()
        );
    }

}
