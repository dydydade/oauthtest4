package login.tikichat.domain.top_ranked_chatroom.member_count.batch;

import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom.repository.ChatRoomRepository;
import login.tikichat.domain.top_ranked_chatroom.member_count.dto.ChatRoomMemberCountStatsDto;
import login.tikichat.domain.top_ranked_chatroom.member_count.model.MemberCountRankedChatRoom;
import login.tikichat.domain.top_ranked_chatroom.member_count.repository.MemberCountRankedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
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
public class SaveMemberCountRankedChatRoomJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberCountRankedChatRoomRepository memberCountRankedChatRoomRepository;
    private static final int TOP_RANKED_CHAT_ROOM_COUNT = 25;
    private static final int CHUNK_SIZE = 1000;

    @Bean(name = "saveMemberCountRankedChatRoomsJob")
    public Job saveMemberCountRankedChatRoomsJob(
            @Qualifier("saveMemberCountRankedChatRoomsFlow") SimpleFlow saveMemberCountRankedChatRoomsFlow
    ) {
        return new JobBuilder("saveMemberCountRankedChatRoomsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(saveMemberCountRankedChatRoomsFlow)
                .end()
                .build();
    }

    @Bean(name = "saveMemberCountRankedChatRoomsFlow")
    public SimpleFlow saveMemberCountRankedChatRoomsFlow(
            @Qualifier("processChatRoomData") Step processChatRoomData,
            @Qualifier("saveMemberCountRankedChatRooms") Step saveMemberCountRankedChatRooms,
            @Qualifier("cleanupOldMemberCountRankedChatRooms") Step cleanupOldMemberCountRankedChatRooms
    ) {
        FlowBuilder<SimpleFlow> flowBuilder = new FlowBuilder<>("checkExecutionStatus");
        return flowBuilder
                .start(memberCountDecider()).on("NOT_EXECUTED_YET_TODAY").to(processChatRoomData)
                .next(saveMemberCountRankedChatRooms)
                .next(cleanupOldMemberCountRankedChatRooms)
                .from(memberCountDecider()).on("COMPLETED_TODAY").stop()
                .build();
    }

    @Bean
    public CheckMemberCountExecutionStatusDecider memberCountDecider() {
        return new CheckMemberCountExecutionStatusDecider(memberCountRankedChatRoomRepository);
    }

    @Bean(name = "processChatRoomData")
    public Step processChatRoomData(RepositoryItemReader<ChatRoom> reader,
                      ItemProcessor<ChatRoom, ChatRoomMemberCountStatsDto> processor,
                      ItemWriter<ChatRoomMemberCountStatsDto> writer) {
        return new StepBuilder("processChatRoomData", jobRepository)
                .<ChatRoom, ChatRoomMemberCountStatsDto>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean(name = "saveMemberCountRankedChatRooms")
    public Step saveMemberCountRankedChatRooms(ItemReader<ChatRoomMemberCountStatsDto> reader,
                      ItemProcessor<ChatRoomMemberCountStatsDto, MemberCountRankedChatRoom> processor,
                      ItemWriter<MemberCountRankedChatRoom> writer) {
        return new StepBuilder("saveMemberCountRankedChatRooms", jobRepository)
                .<ChatRoomMemberCountStatsDto, MemberCountRankedChatRoom>chunk(TOP_RANKED_CHAT_ROOM_COUNT, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean(name = "cleanupOldMemberCountRankedChatRooms")
    public Step cleanupOldMemberCountRankedChatRooms() {
        return new StepBuilder("cleanupOldMemberCountRankedChatRoomsStep", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
                        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
                        memberCountRankedChatRoomRepository.deleteAllOlderThanCutoffDays(sevenDaysAgo);
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public RepositoryItemReader<ChatRoom> chatRoomsReader() {
        return new RepositoryItemReaderBuilder<ChatRoom>()
                .name("chatRoomsReader")
                .repository(chatRoomRepository)
                .methodName("findAll")
                .pageSize(CHUNK_SIZE)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<ChatRoom, ChatRoomMemberCountStatsDto> chatRoomToStatsProcessor() {
        return new ChatRoomToMemberStatsProcessor();
    }

    @Bean
    public ItemWriter<ChatRoomMemberCountStatsDto> chatRoomMemberStatsWriter() {
        return new ChatRoomMemberStatsWriter();
    }

    @Bean
    public ItemReader<ChatRoomMemberCountStatsDto> chatRoomMemberStatsReader() {
        return new ChatRoomMemberStatsReader();
    }

    @Bean
    public ItemProcessor<ChatRoomMemberCountStatsDto, MemberCountRankedChatRoom> statsToMemberCountRankedRoomsProcessor() {
        return new StatsToMemberCountRankedRoomsProcessor();
    }

    @Bean
    public ItemWriter<MemberCountRankedChatRoom> memberCountRankedChatRoomWriter() {
        return new MemberCountRankedChatRoomWriter(memberCountRankedChatRoomRepository);
    }
}
