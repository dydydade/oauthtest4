package login.tikichat.domain.top_ranked_chatroom.member_count.batch;

import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.top_ranked_chatroom.dao.CountRankedChatRoomDao;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@StepScope
@RequiredArgsConstructor
public class ChatRoomWriter implements ItemWriter<ChatRoom> {

    private final CountRankedChatRoomDao chatRoomDao;
    private static final String TEMP_TOTAL_KEY_PREFIX = "rank:total:member:chatroom:hour:";
    private static final String TEMP_CATEGORY_KEY_PREFIX = "rank:category:%s:member:chatroom:hour:";

    @Override
    public void write(Chunk<? extends ChatRoom> items) {
        String thisHour = Instant.now().truncatedTo(ChronoUnit.HOURS).toString();
        String totalKey = TEMP_TOTAL_KEY_PREFIX + thisHour;

        List<? extends ChatRoom> chatRooms = items.getItems();

        chatRoomDao.addChatRoomsToZSetWithPipeline(totalKey, chatRooms);

        chatRooms.stream()
            .collect(Collectors.groupingBy(chatRoom -> chatRoom.getCategory().getCode()))
            .forEach((categoryCode, categoryChatRooms) -> {
                String categoryKey = String.format(TEMP_CATEGORY_KEY_PREFIX, categoryCode) + thisHour;
                chatRoomDao.addChatRoomsToZSetWithPipeline(categoryKey, categoryChatRooms);
            });
    }
}
