package login.tikichat.domain.top_ranked_chatroom.member_count.batch;

import login.tikichat.domain.chatroom.model.ChatRoom;
import login.tikichat.domain.top_ranked_chatroom.member_count.dto.ChatRoomMemberCountStatsDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;


@StepScope
public class ChatRoomToMemberStatsProcessor implements ItemProcessor<ChatRoom, ChatRoomMemberCountStatsDto> {
    @Override
    public ChatRoomMemberCountStatsDto process(ChatRoom chatRoom) throws Exception {
        return new ChatRoomMemberCountStatsDto(chatRoom.getId(), chatRoom.getName(), chatRoom.getCategory().getCode(), chatRoom.getCurrentUserCount(), null, null);
    }
}
