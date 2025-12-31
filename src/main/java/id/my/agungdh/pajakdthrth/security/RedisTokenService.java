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

    public String createToken(String username) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, username, TOKEN_TTL);
        return token;
    }

    public Optional<String> validateToken(String token) {
        String username = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        return Optional.ofNullable(username);
    }

    public void deleteToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }
}
