package login.tikichat.domain.user.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserStatusService {

    private final StringRedisTemplate redisTemplate;
    private final static String USER_STATUS_KEY_PREFIX = "user:status:";

    public UserStatusService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setUserStatus(Long userId, boolean isOnline) {
        redisTemplate.opsForValue().setBit(USER_STATUS_KEY_PREFIX, userId, isOnline);
    }

    public Boolean getUserStatus(Long userId) {
        return redisTemplate.opsForValue().getBit(USER_STATUS_KEY_PREFIX, userId);
    }
}
