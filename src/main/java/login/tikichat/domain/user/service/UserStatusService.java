package login.tikichat.domain.user.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class UserStatusService {

    private final StringRedisTemplate redisTemplate;

    private static final String USER_STATUS_KEY_PREFIX = "user:";
    private static final String USER_STATUS_KEY_SUFFIX = ":status";
    private static final long STATUS_TTL_MINUTES = 3;

    public UserStatusService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setUserStatus(Long userId, boolean isOnline) {
        String key = USER_STATUS_KEY_PREFIX + userId + USER_STATUS_KEY_SUFFIX;
        redisTemplate.opsForValue().set(key, String.valueOf(isOnline), Duration.ofMinutes(STATUS_TTL_MINUTES));
    }

    public Optional<Boolean> getUserStatus(Long userId) {
        String key = USER_STATUS_KEY_PREFIX + userId + USER_STATUS_KEY_SUFFIX;
        String userStatus = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(userStatus).map(Boolean::valueOf);
    }
}
