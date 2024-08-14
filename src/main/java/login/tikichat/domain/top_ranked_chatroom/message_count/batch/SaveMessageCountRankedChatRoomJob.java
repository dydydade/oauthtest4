package login.tikichat.domain.top_ranked_chatroom.message_count.batch;

import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.chat.repository.ChatRepository;
import login.tikichat.domain.top_ranked_chatroom.message_count.dto.ChatRoomMessageCountStatsDto;
import login.tikichat.domain.top_ranked_chatroom.message_count.model.MessageCountRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.message_count.repository.MessageCountRankedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
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
    private final MessageCountRankedChatRoomRepository messageCountRankedChatRoomRepository;
    private static final int CHUNK_SIZE = 1000;

    @Bean(name = "saveMessageCountRankedChatRoomsJob")
    public Job saveMessageCountRankedChatRoomsJob(
            @Qualifier("saveMessageCountRankedChatRoomsFlow") SimpleFlow saveMessageCountRankedChatRoomsFlow
    ) {
        return new JobBuilder("saveMessageCountRankedChatRoomsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(saveMessageCountRankedChatRoomsFlow)
                .end()
                .build();
    }

    @Bean(name = "saveMessageCountRankedChatRoomsFlow")
    public SimpleFlow saveMessageCountRankedChatRoomsFlow(
            @Qualifier("processChatData") Step processChatData,
            @Qualifier("saveMessageCountRankedChatRooms") Step saveMessageCountRankedChatRooms,
            @Qualifier("cleanupOldMessageCountRankedChatRooms") Step cleanupOldMessageCountRankedChatRooms
    ) {
        FlowBuilder<SimpleFlow> flowBuilder = new FlowBuilder<>("checkExecutionStatus");
        return flowBuilder
                .start(messageCountDecider()).on("NOT_EXECUTED_YET_TODAY").to(processChatData)
                .next(saveMessageCountRankedChatRooms)
                .next(cleanupOldMessageCountRankedChatRooms)
                .from(messageCountDecider()).on("COMPLETED_TODAY").stop()
                .build();
    }

    @Bean
    public CheckMessageCountExecutionStatusDecider messageCountDecider() {
        return new CheckMessageCountExecutionStatusDecider(messageCountRankedChatRoomRepository);
    }

    @Bean(name = "processChatData")
    public Step processChatData(RepositoryItemReader<Chat> reader,
                      ItemProcessor<Chat, ChatRoomMessageCountStatsDto> processor,
                      ItemWriter<ChatRoomMessageCountStatsDto> writer) {
        return new StepBuilder("processChatData", jobRepository)
                .<Chat, ChatRoomMessageCountStatsDto>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean(name = "saveMessageCountRankedChatRooms")
    public Step saveMessageCountRankedChatRooms(ItemReader<ChatRoomMessageCountStatsDto> reader,
                      ItemProcessor<ChatRoomMessageCountStatsDto, MessageCountRankedChatRoom> processor,
                      ItemWriter<MessageCountRankedChatRoom> writer) {
        return new StepBuilder("saveMessageCountRankedChatRooms", jobRepository)
                .<ChatRoomMessageCountStatsDto, MessageCountRankedChatRoom>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean(name = "cleanupOldMessageCountRankedChatRooms")
    public Step cleanupOldMessageCountRankedChatRooms() {
        return new StepBuilder("cleanupOldMessageCountRankedChatRooms", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
                        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
                        messageCountRankedChatRoomRepository.deleteAllOlderThanCutoffDays(sevenDaysAgo);
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public RepositoryItemReader<Chat> recentChatsReader() {
        Instant now = Instant.now();
        Instant end = now.truncatedTo(ChronoUnit.DAYS);
        Instant start = end.minus(1, ChronoUnit.DAYS);

        return new RepositoryItemReaderBuilder<Chat>()
                .name("recentChatsReader")
                .repository(chatRepository)
                .methodName("findAllByCreatedDateBetween")
                .arguments(start, end)
                .pageSize(CHUNK_SIZE)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Chat, ChatRoomMessageCountStatsDto> chatToMessageStatsProcessor() {
        return new ChatToMessageStatsProcessor();
    }

    @Bean
    public ItemWriter<ChatRoomMessageCountStatsDto> chatRoomMessageStatsWriter() {
        return new ChatRoomMessageStatsWriter();
    }

    @Bean
    public ItemReader<ChatRoomMessageCountStatsDto> chatRoomMessageStatsReader() {
        return new ChatRoomMessageStatsReader();
    }

    @Bean
    public ItemProcessor<ChatRoomMessageCountStatsDto, MessageCountRankedChatRoom> statsToMessageCountRankedRoomsProcessor() {
        return new StatsToMessageCountRankedRoomsProcessor();
    }

    @Bean
    public ItemWriter<MessageCountRankedChatRoom> messageCountRankedChatRoomWriter() {
        return new MessageCountRankedChatRoomWriter(messageCountRankedChatRoomRepository);
    }
}
