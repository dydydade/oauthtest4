package login.oauthtest4.global.auth.verification.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class AuthTokenDao {

    private static final long EMAIL_VERIFICATION_LIMIT_IN_SECONDS = 600;
    private final StringRedisTemplate redisTemplate;

    public void saveToken(String token) {
        redisTemplate.opsForValue()
                .set(this.getTokenKey(token), "1",
                        Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));
    }

    private String getTokenKey(String token) {
        return "auth_token" + token;
    }

    public boolean hasToken(String token) {
        Boolean keyExists = redisTemplate.hasKey(this.getTokenKey((token)));
        return keyExists != null && keyExists;
    }

    public void removeToken(String token) {
        redisTemplate.delete(this.getTokenKey(token));
    }
}