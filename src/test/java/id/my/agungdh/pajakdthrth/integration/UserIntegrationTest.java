package id.my.agungdh.pajakdthrth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.agungdh.pajakdthrth.BaseIntegrationTest;
import id.my.agungdh.pajakdthrth.dto.AuthResponse;
import id.my.agungdh.pajakdthrth.dto.UserRequest;
import id.my.agungdh.pajakdthrth.model.Role;
import id.my.agungdh.pajakdthrth.model.SKPD;
import id.my.agungdh.pajakdthrth.model.User;
import id.my.agungdh.pajakdthrth.repository.SkpdRepository;
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

import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

public class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkpdRepository skpdRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTokenService redisTokenService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User adminUser;
    private User regularUser;
    private String adminToken;
    private String userToken;
    private SKPD testSkpd;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userRepository.deleteAll();
        skpdRepository.deleteAll();

        // Create SKPD
        testSkpd = new SKPD();
        testSkpd.setNama("Dinas Test");
        testSkpd = skpdRepository.save(testSkpd);

        // Create Admin
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("password"));
        adminUser.setNama("Admin User");
        adminUser.setRole(Role.ADMIN);
        adminUser.setSkpd(testSkpd); // Verify SKPD assignment for admin too
        adminUser = userRepository.save(adminUser);
        adminToken = redisTokenService.createToken(adminUser.getId());

        // Create Regular User
        regularUser = new User();
        regularUser.setUsername("user");
        regularUser.setPassword(passwordEncoder.encode("password"));
        regularUser.setNama("Regular User");
        regularUser.setRole(Role.USER);
        regularUser = userRepository.save(regularUser);
        userToken = redisTokenService.createToken(regularUser.getId());
    }

    @Test
    void create_AsAdmin_Success() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setPassword("newpassword");
        request.setNama("New User");
        request.setRole(Role.USER);
        request.setSkpdId(testSkpd.getUuid());

        mockMvc.perform(post("/api/users")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.skpd.uuid", is(testSkpd.getUuid())));
    }

    @Test
    void create_AsUser_Forbidden() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setPassword("newpassword");
        request.setNama("New User");
        request.setRole(Role.USER);

        mockMvc.perform(post("/api/users")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAll_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThanOrEqualTo(2)));
    }

    @Test
    void findAll_AsUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void findByUuid_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/users/" + regularUser.getUuid())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("user")));
    }

    @Test
    void update_AsAdmin_Success() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("updateduser");
        request.setPassword("updatedpassword");
        request.setNama("Updated Name");
        request.setRole(Role.ADMIN);

        mockMvc.perform(put("/api/users/" + regularUser.getUuid())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updateduser")))
                .andExpect(jsonPath("$.nama", is("Updated Name")))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @Test
    void delete_AsAdmin_Success() throws Exception {
        mockMvc.perform(delete("/api/users/" + regularUser.getUuid())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify soft delete
        mockMvc.perform(get("/api/users/" + regularUser.getUuid())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_DuplicateUsername_Conflict() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("admin"); // Duplicate
        request.setPassword("password");
        request.setNama("Duplicate Admin");
        request.setRole(Role.USER);

        mockMvc.perform(post("/api/users")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
