package login.tikichat.domain.top_ranked_chatroom.message_count.batch;

import login.tikichat.domain.top_ranked_chatroom.message_count.model.MessageCountRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.message_count.repository.MessageCountRankedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.concurrent.ConcurrentHashMap;


@StepScope
@RequiredArgsConstructor
public class MessageCountRankedChatRoomWriter implements ItemWriter<MessageCountRankedChatRoom> {

    private final MessageCountRankedChatRoomRepository messageCountRankedChatRoomRepository;

    @Override
    public void write(Chunk<? extends MessageCountRankedChatRoom> items) {
        items.forEach(messageCountRankedChatRoomRepository::save);
    }
}
