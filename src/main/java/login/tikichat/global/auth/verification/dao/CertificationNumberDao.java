package login.tikichat.global.auth.verification.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Repository
@RequiredArgsConstructor
public class CertificationNumberDao {

    private static final long EMAIL_VERIFICATION_LIMIT_IN_SECONDS = 180;
    private static final String EMAIL_SEND_COUNT_KEY_SUFFIX = ":sendCount";
    private final StringRedisTemplate redisTemplate;

    public void saveCertificationNumber(String email, String certificationNumber) {
        redisTemplate.opsForValue()
                .set(email, certificationNumber,
                        Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));
        this.incrementEmailSendCount(email);
    }

    public String getCertificationNumber(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void removeCertificationNumber(String email) {
        redisTemplate.delete(email);
    }

    public boolean hasKey(String email) {
        Boolean keyExists = redisTemplate.hasKey(email);
        return keyExists != null && keyExists;
    }

    public int getEmailSendCount(String email) {
        String key = email + EMAIL_SEND_COUNT_KEY_SUFFIX;
        String count = redisTemplate.opsForValue().get(key);
        return count == null ? 0 : Integer.parseInt(count);
    }

    public void incrementEmailSendCount(String email) {
        String key = email + EMAIL_SEND_COUNT_KEY_SUFFIX;
        redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expireAt(key, Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
}