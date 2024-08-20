package login.tikichat.domain.chat.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import login.tikichat.domain.chat.dto.ModifyReactionChatEventDto;
import login.tikichat.domain.chat.repository.ChatReactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModifyReactionChatConsumer implements MessageListener {
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatReactionRepository chatReactionRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
            ModifyReactionChatEventDto modifyReactionChatEventDto = objectMapper.readValue(publishMessage, ModifyReactionChatEventDto.class);

            final var chatReaction =
                    this.chatReactionRepository.findById(modifyReactionChatEventDto.getId())
                            .orElseThrow();

            simpMessagingTemplate.convertAndSend(
                    "/sub/chat-rooms/" + chatReaction.getChat().getId() + "/chats/reactions",
                    modifyReactionChatEventDto
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
