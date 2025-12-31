package id.my.agungdh.pajakdthrth.service;

import id.my.agungdh.pajakdthrth.security.RedisTokenService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final RedisTokenService redisTokenService;

    public AuthService(RedisTokenService redisTokenService) {
        this.redisTokenService = redisTokenService;
    }

    public String login(String username, String password) {
        // TODO: Replace with real user authentication
        if ("admin".equals(username) && "admin".equals(password)) {
            return redisTokenService.createToken(username);
        }
        throw new RuntimeException("Invalid credentials");
    }
}
