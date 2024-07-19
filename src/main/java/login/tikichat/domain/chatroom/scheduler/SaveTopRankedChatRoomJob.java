package login.tikichat.domain.chatroom.scheduler;

import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.chat.repository.ChatRepository;
import login.tikichat.domain.top_ranked_chatroom.model.TopRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.repository.TopRankedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
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
public class SaveTopRankedChatRoomJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ChatRepository chatRepository;
    private final TopRankedChatRoomRepository topRankedChatRoomRepository;
    private static final int TOP_RANKED_CHAT_ROOM_COUNT = 25;
    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Job saveTopRankedChatRoomsJob(@Qualifier("processChatData") Step processChatData, @Qualifier("saveTopRankedChatRooms") Step saveTopRankedChatRooms) {
        return new JobBuilder("saveTopRankedChatRoomsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(processChatData)
                .next(saveTopRankedChatRooms)
                .build();
    }

    @Bean(name = "processChatData")
    public Step processChatData(RepositoryItemReader<Chat> reader,
                      ItemProcessor<Chat, ChatRoomStatsDto> processor,
                      ItemWriter<ChatRoomStatsDto> writer) {
        return new StepBuilder("processChatData", jobRepository)
                .<Chat, ChatRoomStatsDto>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean(name = "saveTopRankedChatRooms")
    public Step saveTopRankedChatRooms(ItemReader<ChatRoomStatsDto> reader,
                      ItemProcessor<ChatRoomStatsDto, TopRankedChatRoom> processor,
                      ItemWriter<TopRankedChatRoom> writer) {
        return new StepBuilder("saveTopRankedChatRooms", jobRepository)
                .<ChatRoomStatsDto, TopRankedChatRoom>chunk(TOP_RANKED_CHAT_ROOM_COUNT, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
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
    public ItemProcessor<Chat, ChatRoomStatsDto> chatToStatsProcessor() {
        return new ChatToStatsProcessor();
    }

    @Bean
    public ItemWriter<ChatRoomStatsDto> chatStatsWriter() {
        return new ChatStatsWriter();
    }

    @Bean
    public ItemReader<ChatRoomStatsDto> chatStatsReader() {
        return new ChatStatsReader();
    }

    @Bean
    public ItemProcessor<ChatRoomStatsDto, TopRankedChatRoom> statsToTopRankedRoomsProcessor() {
        return new StatsToTopRankedRoomsProcessor();
    }

    @Bean
    public ItemWriter<TopRankedChatRoom> topRankedChatRoomWriter() {
        return new TopRankedChatRoomWriter(topRankedChatRoomRepository);
    }
}
