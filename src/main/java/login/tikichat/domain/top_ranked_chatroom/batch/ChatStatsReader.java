package login.tikichat.domain.top_ranked_chatroom.batch;

import login.tikichat.domain.top_ranked_chatroom.dto.ChatRoomStatsDto;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@StepScope
public class ChatStatsReader implements ItemReader<ChatRoomStatsDto> {

    private Iterator<ChatRoomStatsDto> iterator;
    private static final int TOP_RANKED_CHAT_ROOM_COUNT = 25;

    private void initialize() {
        ExecutionContext context = StepSynchronizationManager.getContext().getStepExecution().getJobExecution().getExecutionContext();
        ConcurrentHashMap<Long, ChatRoomStatsDto> chatRoomStatsMap = (ConcurrentHashMap<Long, ChatRoomStatsDto>) context.get("chatRoomStatsMap");

        if (chatRoomStatsMap != null) {
            updateRankings(chatRoomStatsMap);

            List<ChatRoomStatsDto> sortedChatRooms = chatRoomStatsMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.comparing(ChatRoomStatsDto::getMessageCount).reversed()))
                    .limit(TOP_RANKED_CHAT_ROOM_COUNT)
                    .map(Map.Entry::getValue)
                    .toList();

            iterator = sortedChatRooms.iterator();
        }
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        initialize();
    }

    @Override
    public ChatRoomStatsDto read() throws Exception {
        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }

    private void updateRankings(ConcurrentHashMap<Long, ChatRoomStatsDto> chatRoomStatsMap) {
        List<ChatRoomStatsDto> rankedList = new ArrayList<>(chatRoomStatsMap.values());
        rankedList.sort(Comparator.comparing(ChatRoomStatsDto::getMessageCount).reversed());

        int rank = 1;
        for (ChatRoomStatsDto stats : rankedList) {
            stats.setRank(rank++);
        }
    }
}
