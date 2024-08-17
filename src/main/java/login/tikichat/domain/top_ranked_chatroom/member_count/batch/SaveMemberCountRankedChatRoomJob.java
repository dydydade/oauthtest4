package login.tikichat.domain.top_ranked_chatroom.member_count.batch;

import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.chatroom.repository.ChatRoomRepository;
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

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class SaveMemberCountRankedChatRoomJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ChatRoomRepository chatRoomRepository;
    private final CountRankedChatRoomDao chatRoomDao;
    private final MemberKeyRenameListener memberKeyRenameListener;

    private static final int CHUNK_SIZE = 10000;

    @Bean(name = "saveMemberCountRankedChatRoomsJob")
    public Job saveMemberCountRankedChatRoomsJob(
            @Qualifier("saveMemberCountRankedChatRoomsFlow") SimpleFlow saveMemberCountRankedChatRoomsFlow
    ) {
        return new JobBuilder("saveMemberCountRankedChatRoomsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(saveMemberCountRankedChatRoomsFlow)
                .end()
                .listener(memberKeyRenameListener)
                .build();
    }

    @Bean(name = "saveMemberCountRankedChatRoomsFlow")
    public SimpleFlow saveMemberCountRankedChatRoomsFlow(
            @Qualifier("processChatRoomData") Step processChatRoomData
    ) {
        FlowBuilder<SimpleFlow> flowBuilder = new FlowBuilder<>("checkExecutionStatus");
        return flowBuilder
                .start(processChatRoomData)
                .build();
    }

    @Bean(name = "processChatRoomData")
    public Step processChatRoomData(RepositoryItemReader<ChatRoom> reader,
                      ItemWriter<ChatRoom> writer) {
        return new StepBuilder("processChatRoomData", jobRepository)
                .<ChatRoom, ChatRoom>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .writer(writer)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
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
    public ItemWriter<ChatRoom> chatRoomWriter() {
        return new ChatRoomWriter(chatRoomDao);
    }
}
