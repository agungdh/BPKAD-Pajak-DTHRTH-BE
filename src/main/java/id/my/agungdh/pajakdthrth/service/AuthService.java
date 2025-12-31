package id.my.agungdh.pajakdthrth.service;

import id.my.agungdh.pajakdthrth.model.User;
import id.my.agungdh.pajakdthrth.repository.UserRepository;
import id.my.agungdh.pajakdthrth.security.RedisTokenService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final RedisTokenService redisTokenService;
    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AuthService(RedisTokenService redisTokenService, UserRepository userRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.redisTokenService = redisTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return redisTokenService.createToken(user.getId());
    }
}
