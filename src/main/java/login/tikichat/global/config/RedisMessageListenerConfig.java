package login.tikichat.global.config;

import login.tikichat.domain.chat.pubsub.ModifyReactionChatConsumer;
import login.tikichat.domain.chat.pubsub.SendChatConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisMessageListenerConfig {
    private final ChannelTopic chatChannelTopic;
    private final ChannelTopic chatReactionChannelTopic;
    private final SendChatConsumer sendChatConsumer;
    private final ModifyReactionChatConsumer modifyReactionChatConsumer;

    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(sendChatConsumer, chatChannelTopic);
        container.addMessageListener(modifyReactionChatConsumer, chatReactionChannelTopic);

        return container;
    }
}
