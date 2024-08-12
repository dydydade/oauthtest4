package login.tikichat.domain.top_ranked_chatroom.message_count.batch;

import jakarta.validation.constraints.NotNull;
import login.tikichat.domain.top_ranked_chatroom.message_count.dto.ChatRoomMessageCountStatsDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.concurrent.ConcurrentHashMap;

@StepScope
public class ChatRoomMessageStatsWriter implements ItemWriter<ChatRoomMessageCountStatsDto> {

    @Override
    public void write(@NotNull Chunk<? extends ChatRoomMessageCountStatsDto> items) {
        ExecutionContext context = StepSynchronizationManager.getContext().getStepExecution().getJobExecution().getExecutionContext();
        ConcurrentHashMap<Long, ChatRoomMessageCountStatsDto> chatRoomStatsMap = (ConcurrentHashMap<Long, ChatRoomMessageCountStatsDto>) context.get("chatRoomStatsMap");

        if (chatRoomStatsMap == null) {
            chatRoomStatsMap = new ConcurrentHashMap<>();
        }

        for (ChatRoomMessageCountStatsDto item : items) {
            chatRoomStatsMap.merge(item.getChatRoomId(), item, (existingValue, newValue) -> {
                existingValue.incrementCount(newValue.getMessageCount());
                return existingValue;
            });
        }
        context.put("chatRoomStatsMap", chatRoomStatsMap);
    }
}
