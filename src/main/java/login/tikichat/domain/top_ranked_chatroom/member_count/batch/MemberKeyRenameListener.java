package login.tikichat.domain.top_ranked_chatroom.member_count.batch;

import login.tikichat.domain.category.repository.CategoryRepository;
import login.tikichat.domain.top_ranked_chatroom.dao.CountRankedChatRoomDao;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class MemberKeyRenameListener implements JobExecutionListener {

    private final CountRankedChatRoomDao chatRoomDao;
    private final CategoryRepository categoryRepository;

    private static final String TEMP_TOTAL_KEY_PREFIX = "rank:total:member:chatroom:hour:";
    private static final String TEMP_CATEGORY_KEY_PREFIX = "rank:category:%s:member:chatroom:hour:";
    private static final String TOTAL_KEY_PREFIX = "rank:total:member:chatroom";
    private static final String CATEGORY_KEY_PREFIX = "rank:category:%s:member:chatroom";

    @Override
    public void afterJob(JobExecution jobExecution) {
        String thisHour = Instant.now().truncatedTo(ChronoUnit.HOURS).toString();

        renameKey(TEMP_TOTAL_KEY_PREFIX + thisHour, TOTAL_KEY_PREFIX);
        setExpire(TOTAL_KEY_PREFIX, Duration.ofDays(3));

        categoryRepository.findAll().forEach(category -> {
            String categoryKey = String.format(TEMP_CATEGORY_KEY_PREFIX, category.getCode()) + thisHour;
            String newCategoryKey = String.format(CATEGORY_KEY_PREFIX, category.getCode());
            renameKey(categoryKey, newCategoryKey);
            setExpire(newCategoryKey, Duration.ofDays(3));
        });
    }


    private void renameKey(String oldKey, String newKey) {
        if (chatRoomDao.checkIfKeyExists(oldKey)) {
            chatRoomDao.rename(oldKey, newKey);
        }
    }

    private void setExpire(String key, Duration duration) {
        if (chatRoomDao.checkIfKeyExists(key)) {
            chatRoomDao.setExpire(key, duration);
        }
    }
}
