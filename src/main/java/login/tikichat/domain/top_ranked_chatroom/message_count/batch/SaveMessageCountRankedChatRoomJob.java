package login.tikichat.domain.top_ranked_chatroom.message_count.batch;

import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.chat.repository.ChatRepository;
import login.tikichat.domain.top_ranked_chatroom.dao.CountRankedChatRoomDao;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class SaveMessageCountRankedChatRoomJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ChatRepository chatRepository;
    private final CountRankedChatRoomDao chatRoomDao;
    private final MessageKeyRenameListener messageKeyRenameListener;

    private static final int CHUNK_SIZE = 300;

    @Bean(name = "saveMessageCountRankedChatRoomsJob")
    public Job saveMessageCountRankedChatRoomsJob(
            @Qualifier("saveMessageCountRankedChatRoomsFlow") SimpleFlow saveMessageCountRankedChatRoomsFlow
    ) {
        return new JobBuilder("saveMessageCountRankedChatRoomsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(saveMessageCountRankedChatRoomsFlow)
                .end()
                .listener(messageKeyRenameListener)
                .build();
    }

    @Bean(name = "saveMessageCountRankedChatRoomsFlow")
    public SimpleFlow saveMessageCountRankedChatRoomsFlow(
            @Qualifier("processChatData") Step processChatData
    ) {
        FlowBuilder<SimpleFlow> flowBuilder = new FlowBuilder<>("checkExecutionStatus");
        return flowBuilder
                .start(processChatData)
                .build();
    }

    @Bean(name = "processChatData")
    public Step processChatData(RepositoryItemReader<Chat> reader,
                      ItemWriter<Chat> writer) {
        return new StepBuilder("processChatData", jobRepository)
                .<Chat, Chat>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .writer(writer)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean
    public RepositoryItemReader<Chat> recentChatsReader() {
        Instant now = Instant.now().plus(30, ChronoUnit.DAYS);
        Instant end = now.truncatedTo(ChronoUnit.HOURS);
        Instant start = end.minus(60, ChronoUnit.DAYS);

        return new RepositoryItemReaderBuilder<Chat>()
                .name("recentChatsReader")
                .repository(chatRepository)
                .methodName("findAllByCreatedDateBetween")
                .arguments(start, now)
                .pageSize(CHUNK_SIZE)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemWriter<Chat> chatWriter() {
        return new ChatWriter(chatRoomDao);
    }
}
