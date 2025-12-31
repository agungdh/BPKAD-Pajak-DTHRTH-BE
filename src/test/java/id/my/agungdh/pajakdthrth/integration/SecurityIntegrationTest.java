package id.my.agungdh.pajakdthrth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.agungdh.pajakdthrth.dto.AuthRequest;
import id.my.agungdh.pajakdthrth.model.Role;
import id.my.agungdh.pajakdthrth.model.User;
import id.my.agungdh.pajakdthrth.repository.UserRepository;
import id.my.agungdh.pajakdthrth.security.RedisTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTokenService redisTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Setup MockMvc with Spring Security
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userRepository.deleteAll();
    }

    @Test
    void accessProtectedEndpoint_WithoutToken_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/skpd"))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessProtectedEndpoint_WithInvalidToken_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/skpd")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessProtectedEndpoint_WithValidToken_ShouldReturnOk() throws Exception {
        // 1. Create User
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setNama("Test User");
        user.setRole(Role.USER);
        userRepository.save(user);

        // 2. Generate Token directly
        String token = redisTokenService.createToken(user.getId());

        // 3. Access Protected Endpoint
        mockMvc.perform(get("/api/skpd")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void loginEndpoint_IsPublic() throws Exception {
        // 1. Create User
        User user = new User();
        user.setUsername("loginuser");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setNama("Login User");
        user.setRole(Role.USER);
        userRepository.save(user);

        // 2. Attempt Login
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername("loginuser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }
}
