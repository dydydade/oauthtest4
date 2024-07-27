package login.tikichat.domain.top_ranked_chatroom.batch;

import login.tikichat.domain.top_ranked_chatroom.model.TopRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.repository.TopRankedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.concurrent.ConcurrentHashMap;


@StepScope
@RequiredArgsConstructor
public class TopRankedChatRoomWriter implements ItemWriter<TopRankedChatRoom> {

    private final TopRankedChatRoomRepository topRankedChatRoomRepository;

    @Override
    public void write(Chunk<? extends TopRankedChatRoom> items) {
        ExecutionContext context = StepSynchronizationManager.getContext().getStepExecution().getJobExecution().getExecutionContext();
        ConcurrentHashMap<Long, Integer> chatRoomStatsMap = (ConcurrentHashMap<Long, Integer>) context.get("chatRoomStatsMap");
        items.forEach(topRankedChatRoomRepository::save);
    }
}
