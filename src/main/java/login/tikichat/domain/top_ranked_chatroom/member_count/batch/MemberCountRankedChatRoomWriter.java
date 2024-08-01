package login.tikichat.domain.top_ranked_chatroom.member_count.batch;

import login.tikichat.domain.top_ranked_chatroom.member_count.model.MemberCountRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.member_count.repository.MemberCountRankedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.concurrent.ConcurrentHashMap;


@StepScope
@RequiredArgsConstructor
public class MemberCountRankedChatRoomWriter implements ItemWriter<MemberCountRankedChatRoom> {

    private final MemberCountRankedChatRoomRepository memberCountRankedChatRoomRepository;

    @Override
    public void write(Chunk<? extends MemberCountRankedChatRoom> items) {
        ExecutionContext context = StepSynchronizationManager.getContext().getStepExecution().getJobExecution().getExecutionContext();
        ConcurrentHashMap<Long, Integer> chatRoomStatsMap = (ConcurrentHashMap<Long, Integer>) context.get("chatRoomStatsMap");
        items.forEach(memberCountRankedChatRoomRepository::save);
    }
}
