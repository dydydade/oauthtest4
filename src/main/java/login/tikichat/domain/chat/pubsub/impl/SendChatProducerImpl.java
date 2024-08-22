package login.tikichat.domain.chat.pubsub.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import login.tikichat.domain.chat.dto.ModifyReactionChatEventDto;
import login.tikichat.domain.chat.pubsub.SendChatProducer;
import login.tikichat.domain.chat.dto.SendChatEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendChatProducerImpl implements SendChatProducer {
    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic chatChannelTopic;
    private final ChannelTopic chatReactionChannelTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void sendMessage(SendChatEventDto sendChatEventDto) {
        try {
            this.redisTemplate.convertAndSend(
                    chatChannelTopic.getTopic(),
                    objectMapper.writeValueAsString(sendChatEventDto)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void modifyReaction(ModifyReactionChatEventDto modifyReactionChatEventDto) {
        try {
            this.redisTemplate.convertAndSend(
                    chatReactionChannelTopic.getTopic(),
                    objectMapper.writeValueAsString(modifyReactionChatEventDto)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
