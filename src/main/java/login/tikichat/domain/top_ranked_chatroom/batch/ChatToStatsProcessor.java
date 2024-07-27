package login.tikichat.domain.top_ranked_chatroom.batch;

import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.top_ranked_chatroom.dto.ChatRoomStatsDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;


@StepScope
public class ChatToStatsProcessor implements ItemProcessor<Chat, ChatRoomStatsDto> {
    @Override
    public ChatRoomStatsDto process(Chat chat) throws Exception {
        return new ChatRoomStatsDto(chat.getChatRoom().getId(), 1, null);
    }
}
