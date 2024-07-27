package login.tikichat.domain.top_ranked_chatroom.batch;

import jakarta.validation.constraints.NotNull;
import login.tikichat.domain.top_ranked_chatroom.dto.ChatRoomStatsDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.concurrent.ConcurrentHashMap;

@StepScope
public class ChatStatsWriter implements ItemWriter<ChatRoomStatsDto> {

    @Override
    public void write(@NotNull Chunk<? extends ChatRoomStatsDto> items) {
        ExecutionContext context = StepSynchronizationManager.getContext().getStepExecution().getJobExecution().getExecutionContext();
        ConcurrentHashMap<Long, ChatRoomStatsDto> chatRoomStatsMap = (ConcurrentHashMap<Long, ChatRoomStatsDto>) context.get("chatRoomStatsMap");

        if (chatRoomStatsMap == null) {
            chatRoomStatsMap = new ConcurrentHashMap<>();
        }

        for (ChatRoomStatsDto item : items) {
            chatRoomStatsMap.merge(item.getChatRoomId(), item, (existingValue, newValue) -> {
                existingValue.incrementCount(newValue.getMessageCount());
                return existingValue;
            });
        }
        context.put("chatRoomStatsMap", chatRoomStatsMap);
    }
}
