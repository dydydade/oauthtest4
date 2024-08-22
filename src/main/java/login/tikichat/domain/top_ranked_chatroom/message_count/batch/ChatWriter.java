package login.tikichat.domain.top_ranked_chatroom.message_count.batch;

import login.tikichat.domain.chat.model.Chat;
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
public class ChatWriter implements ItemWriter<Chat> {

    private final CountRankedChatRoomDao chatRoomDao;
    private static final String TEMP_TOTAL_KEY_PREFIX = "rank:total:message:chatroom:hour:";
    private static final String TEMP_CATEGORY_KEY_PREFIX = "rank:category:%s:message:chatroom:hour:";

    @Override
    public void write(Chunk<? extends Chat> items) {
        String thisHour = Instant.now().truncatedTo(ChronoUnit.HOURS).toString();
        String totalKey = TEMP_TOTAL_KEY_PREFIX + thisHour;

        List<? extends Chat> chats = items.getItems();

        chatRoomDao.addChatsToZSetWithPipeline(totalKey, chats);

        chats.stream()
            .collect(Collectors.groupingBy(chat -> chat.getChatRoom().getCategory().getCode()))
            .forEach((categoryCode, categoryChats) -> {
                String categoryKey = String.format(TEMP_CATEGORY_KEY_PREFIX, categoryCode) + thisHour;
                chatRoomDao.addChatsToZSetWithPipeline(categoryKey, categoryChats);
            });
    }
}
