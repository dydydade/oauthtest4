package login.tikichat.domain.top_ranked_chatroom.member_count.batch;

import login.tikichat.domain.top_ranked_chatroom.member_count.repository.MemberCountRankedChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class CheckMemberCountExecutionStatusDecider implements JobExecutionDecider {

    private final MemberCountRankedChatRoomRepository repository;

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        if (checkJobExecutionStatus()) {
            return new FlowExecutionStatus("COMPLETED_TODAY");
        }
        return new FlowExecutionStatus("NOT_EXECUTED_YET_TODAY");
    }

    private boolean checkJobExecutionStatus() {
        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return repository.existsByReportDateBetween(startOfDay, endOfDay);
    }
}
