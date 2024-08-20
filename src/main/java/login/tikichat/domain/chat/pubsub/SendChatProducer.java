package login.tikichat.domain.chat.pubsub;

import login.tikichat.domain.chat.dto.ModifyReactionChatEventDto;
import login.tikichat.domain.chat.dto.SendChatEventDto;

public interface SendChatProducer {
    void sendMessage(SendChatEventDto sendChatEventDto);
    void modifyReaction(ModifyReactionChatEventDto modifyReactionChatEventDto);
}
