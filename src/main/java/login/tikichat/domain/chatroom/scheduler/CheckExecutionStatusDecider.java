package login.tikichat.domain.chatroom.scheduler;

import login.tikichat.domain.top_ranked_chatroom.repository.TopRankedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckExecutionStatusDecider implements JobExecutionDecider {

    private final TopRankedChatRoomRepository repository;

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        if (checkJobExecutionStatus()) {
            return new FlowExecutionStatus("COMPLETED_TODAY");
        }

        return new FlowExecutionStatus("NOT_EXECUTED_YET_TODAY");
    }


    public boolean checkJobExecutionStatus() {
        // TODO: 당일 Job 이 이미 실행되어 집계값(TopRankedChatRoom 엔티티)가 저장되었는지 체크
        return true;
    }
}
