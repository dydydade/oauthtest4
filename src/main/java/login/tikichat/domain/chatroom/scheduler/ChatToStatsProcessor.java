package login.tikichat.domain.chatroom.scheduler;

import login.tikichat.domain.chat.model.Chat;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;


@StepScope
public class ChatToStatsProcessor implements ItemProcessor<Chat, ChatRoomStatsDto> {
    @Override
    public ChatRoomStatsDto process(Chat chat) throws Exception {
        return new ChatRoomStatsDto(chat.getChatRoom().getId(), 1, null);
    }
}
