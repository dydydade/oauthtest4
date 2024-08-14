package login.tikichat.domain.top_ranked_chatroom.message_count.batch;

import login.tikichat.domain.top_ranked_chatroom.message_count.dto.ChatRoomMessageCountStatsDto;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@StepScope
public class ChatRoomMessageStatsReader implements ItemReader<ChatRoomMessageCountStatsDto> {

    private Iterator<ChatRoomMessageCountStatsDto> iterator;

    private void initialize() {
        ExecutionContext context = StepSynchronizationManager.getContext().getStepExecution().getJobExecution().getExecutionContext();
        ConcurrentHashMap<Long, ChatRoomMessageCountStatsDto> chatRoomStatsMap = (ConcurrentHashMap<Long, ChatRoomMessageCountStatsDto>) context.get("chatRoomStatsMap");

        if (chatRoomStatsMap != null) {
            updateInnerCategoryRankings(chatRoomStatsMap);
            updateTotalRankings(chatRoomStatsMap);

            List<ChatRoomMessageCountStatsDto> sortedChatRooms = chatRoomStatsMap.values()
                    .stream()
                    .toList();

            iterator = sortedChatRooms.iterator();
        }
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        initialize();
    }

    @Override
    public ChatRoomMessageCountStatsDto read() throws Exception {
        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }

    private void updateTotalRankings(ConcurrentHashMap<Long, ChatRoomMessageCountStatsDto> chatRoomStatsMap) {
        List<ChatRoomMessageCountStatsDto> rankedList = new ArrayList<>(chatRoomStatsMap.values());
        rankedList.sort(Comparator.comparing(ChatRoomMessageCountStatsDto::getMessageCount).reversed());

        int rank = 1;
        for (ChatRoomMessageCountStatsDto stats : rankedList) {
            stats.setRank(rank++);
        }
    }

    private void updateInnerCategoryRankings(ConcurrentHashMap<Long, ChatRoomMessageCountStatsDto> chatRoomStatsMap) {
        Map<String, List<ChatRoomMessageCountStatsDto>> sortedChatRoomsByCategory = chatRoomStatsMap.values().stream()
                .collect(Collectors.groupingBy(ChatRoomMessageCountStatsDto::getCategoryCode))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .sorted(Comparator.comparingInt(ChatRoomMessageCountStatsDto::getMessageCount).reversed()
                                        .thenComparing(ChatRoomMessageCountStatsDto::getChatRoomName))
                                .collect(Collectors.toList())
                ));

        sortedChatRoomsByCategory.forEach((category, chats) -> {
            IntStream.range(0, chats.size())
                    .forEach(index -> chats.get(index).setInnerCategoryRank(index + 1));
        });

        chatRoomStatsMap.clear();

        sortedChatRoomsByCategory.values().stream()
                .flatMap(List::stream)
                .forEach(dto -> chatRoomStatsMap.put(dto.getChatRoomId(), dto));
    }
}
