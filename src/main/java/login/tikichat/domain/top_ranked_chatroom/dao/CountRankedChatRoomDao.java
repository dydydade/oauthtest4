package login.tikichat.domain.top_ranked_chatroom.dao;

import login.tikichat.domain.chat.model.Chat;
import login.tikichat.domain.chatroom.model.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CountRankedChatRoomDao {

    private final StringRedisTemplate redisTemplate;

    public void setExpire(String rankKey, Duration duration) {
        redisTemplate.expire(rankKey, duration);
    }

    public List<String> getChatRoomRank(String rankKey, int start, int end) {
        return new ArrayList<>(redisTemplate.opsForZSet().reverseRange(rankKey, start, end));
    }

    public Double getChatRoomScore(String rankKey, String chatRoomId) {
        return redisTemplate.opsForZSet().score(rankKey, chatRoomId);
    }

    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    public boolean checkIfKeyExists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }

    public void addChatsToZSetWithPipeline(String key, List<? extends Chat> chats) {
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            chats.forEach(chat -> {
                connection.zSetCommands().zIncrBy(
                        stringSerializer.serialize(key),
                        1,
                        stringSerializer.serialize(String.valueOf(chat.getChatRoom().getId()))
                );
            });
            return null;
        });
    }

    public void addChatRoomsToZSetWithPipeline(String key, List<? extends ChatRoom> chatRooms) {
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            chatRooms.forEach(chatRoom -> {
                connection.zSetCommands().zIncrBy(
                        stringSerializer.serialize(key),
                        chatRoom.getCurrentUserCount(),
                        stringSerializer.serialize(String.valueOf(chatRoom.getId()))
                );
            });
            return null;
        });
    }

    public void append(String key, String value) {
        redisTemplate.opsForValue().append(key, value);
    }
}
