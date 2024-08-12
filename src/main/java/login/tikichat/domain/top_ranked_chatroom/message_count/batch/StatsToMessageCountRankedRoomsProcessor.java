package login.tikichat.domain.top_ranked_chatroom.message_count.batch;

import login.tikichat.domain.top_ranked_chatroom.message_count.dto.ChatRoomMessageCountStatsDto;
import login.tikichat.domain.top_ranked_chatroom.message_count.model.MessageCountRankedChatRoom;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;


@StepScope
public class StatsToMessageCountRankedRoomsProcessor implements ItemProcessor<ChatRoomMessageCountStatsDto, MessageCountRankedChatRoom> {

    @Override
    public MessageCountRankedChatRoom process(ChatRoomMessageCountStatsDto stats) {
        return new MessageCountRankedChatRoom(
                stats.getChatRoomId(),
                stats.getChatRoomName(),
                stats.getMessageCount(),
                stats.getRank(),
                stats.getCategoryCode(),
                stats.getInnerCategoryRank()
        );
    }
}
