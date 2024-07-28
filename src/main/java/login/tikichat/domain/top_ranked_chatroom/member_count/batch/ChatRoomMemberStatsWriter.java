package login.tikichat.domain.top_ranked_chatroom.member_count.batch;

import jakarta.validation.constraints.NotNull;
import login.tikichat.domain.top_ranked_chatroom.member_count.dto.ChatRoomMemberCountStatsDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.concurrent.ConcurrentHashMap;

@StepScope
public class ChatRoomMemberStatsWriter implements ItemWriter<ChatRoomMemberCountStatsDto> {

    @Override
    public void write(@NotNull Chunk<? extends ChatRoomMemberCountStatsDto> items) {
        ExecutionContext context = StepSynchronizationManager.getContext().getStepExecution().getJobExecution().getExecutionContext();
        ConcurrentHashMap<Long, ChatRoomMemberCountStatsDto> chatRoomStatsMap = (ConcurrentHashMap<Long, ChatRoomMemberCountStatsDto>) context.get("chatRoomStatsMap");

        if (chatRoomStatsMap == null) {
            chatRoomStatsMap = new ConcurrentHashMap<>();
        }

        for (ChatRoomMemberCountStatsDto item : items) {
            chatRoomStatsMap.merge(item.getChatRoomId(), item, (existingValue, newValue) -> {
                existingValue.incrementCount(newValue.getMemberCount());
                return existingValue;
            });
        }
        context.put("chatRoomStatsMap", chatRoomStatsMap);
    }
}
