package login.tikichat.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.mail.host}")
    private String host;
    @Value("${spring.data.redis.mail.port}")
    private int port;

    @Bean
    public LettuceConnectionFactory redisMailConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory redisMailConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisMailConnectionFactory);
        return stringRedisTemplate;
    }

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate2(RedisConnectionFactory redisMailConnectionFactory) {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisMailConnectionFactory);
//
//        return redisTemplate;
//    }

    @Bean
    public ChannelTopic chatChannelTopic() {
        return new ChannelTopic("chat");
    }

    @Bean
    public ChannelTopic chatReactionChannelTopic() {
        return new ChannelTopic("chat");
    }
}
