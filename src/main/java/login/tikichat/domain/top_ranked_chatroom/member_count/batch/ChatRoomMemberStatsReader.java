package login.tikichat.domain.top_ranked_chatroom.member_count.batch;

import login.tikichat.domain.top_ranked_chatroom.member_count.dto.ChatRoomMemberCountStatsDto;
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
public class ChatRoomMemberStatsReader implements ItemReader<ChatRoomMemberCountStatsDto> {

    private Iterator<ChatRoomMemberCountStatsDto> iterator;
    private static final int TOP_RANKED_CHAT_ROOM_COUNT = 25;

    private void initialize() {
        ExecutionContext context = StepSynchronizationManager.getContext().getStepExecution().getJobExecution().getExecutionContext();
        ConcurrentHashMap<Long, ChatRoomMemberCountStatsDto> chatRoomStatsMap = (ConcurrentHashMap<Long, ChatRoomMemberCountStatsDto>) context.get("chatRoomStatsMap");

        if (chatRoomStatsMap != null) {
            updateInnerCategoryRankings(chatRoomStatsMap);
            updateTotalRankings(chatRoomStatsMap);

            List<ChatRoomMemberCountStatsDto> sortedChatRooms = chatRoomStatsMap.entrySet()
                    .stream()
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
    public ChatRoomMemberCountStatsDto read() throws Exception {
        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }

    private void updateTotalRankings(ConcurrentHashMap<Long, ChatRoomMemberCountStatsDto> chatRoomStatsMap) {
        List<ChatRoomMemberCountStatsDto> rankedList = new ArrayList<>(chatRoomStatsMap.values());
        rankedList.sort(Comparator.comparing(ChatRoomMemberCountStatsDto::getMemberCount).reversed());

        int rank = 1;
        for (ChatRoomMemberCountStatsDto stats : rankedList) {
            stats.setRank(rank++);
        }
    }

    private void updateInnerCategoryRankings(ConcurrentHashMap<Long, ChatRoomMemberCountStatsDto> chatRoomStatsMap) {
        Map<String, List<ChatRoomMemberCountStatsDto>> sortedChatRoomsByCategory = chatRoomStatsMap.values().stream()
                .collect(Collectors.groupingBy(ChatRoomMemberCountStatsDto::getCategoryCode))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .sorted(Comparator.comparingInt(ChatRoomMemberCountStatsDto::getMemberCount).reversed()
                                        .thenComparing(ChatRoomMemberCountStatsDto::getChatRoomName))
                                .limit(TOP_RANKED_CHAT_ROOM_COUNT)
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
