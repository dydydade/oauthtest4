package login.tikichat.domain.top_ranked_chatroom.message_count.batch;

import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.top_ranked_chatroom.message_count.dto.ChatRoomMessageCountStatsDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;


@StepScope
public class ChatToMessageStatsProcessor implements ItemProcessor<Chat, ChatRoomMessageCountStatsDto> {
    @Override
    public ChatRoomMessageCountStatsDto process(Chat chat) throws Exception {
        return new ChatRoomMessageCountStatsDto(chat.getChatRoom().getId(), chat.getChatRoom().getName(), chat.getChatRoom().getCategory().getCode(), 1, null, null);
    }
}
