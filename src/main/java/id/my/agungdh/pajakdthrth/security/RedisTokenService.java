package id.my.agungdh.pajakdthrth.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class RedisTokenService {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TOKEN_TTL = Duration.ofHours(1);
    private static final String TOKEN_PREFIX = "auth_token:";

    public RedisTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, userId.toString(), TOKEN_TTL);
        return token;
    }

    public Optional<Long> validateToken(String token) {
        String userIdStr = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        return Optional.ofNullable(userIdStr).map(Long::valueOf);
    }

    public void deleteToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }
}
