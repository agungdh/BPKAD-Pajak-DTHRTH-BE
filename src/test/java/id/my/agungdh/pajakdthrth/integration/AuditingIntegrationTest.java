package id.my.agungdh.pajakdthrth.integration;

import id.my.agungdh.pajakdthrth.model.Role;
import id.my.agungdh.pajakdthrth.model.SKPD;
import id.my.agungdh.pajakdthrth.model.User;
import id.my.agungdh.pajakdthrth.repository.SkpdRepository;
import id.my.agungdh.pajakdthrth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

import id.my.agungdh.pajakdthrth.BaseIntegrationTest;

public class AuditingIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SkpdRepository skpdRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clear repositories
        skpdRepository.deleteAll();
        userRepository.deleteAll();

        // Create a user in DB (needed so we can get a valid ID)
        User user = new User();
        user.setUsername("auditor");
        user.setPassword("secret");
        user.setNama("Auditor User");
        user.setRole(Role.ADMIN);
        testUser = userRepository.save(user);

        // Mock SecurityContext with User ID (Long)
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                testUser.getId(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void whenCreateEntity_thenCreatedByIsSetToUserId() {
        SKPD skpd = new SKPD();
        skpd.setNama("Dinas Test Auditing");

        SKPD savedSkpd = skpdRepository.save(skpd);

        assertThat(savedSkpd.getCreatedBy()).isNotNull();
        assertThat(savedSkpd.getCreatedBy()).isEqualTo(testUser.getId());
        assertThat(savedSkpd.getUpdatedBy()).isEqualTo(testUser.getId());
        assertThat(savedSkpd.getCreatedAt()).isNotNull();
    }

    @Test
    void whenUpdateEntity_thenUpdatedByIsSetToUserId() {
        SKPD skpd = new SKPD();
        skpd.setNama("Before Update");
        skpd = skpdRepository.save(skpd);

        // Simulate update by same user (or different one if we changed context)
        skpd.setNama("After Update");
        SKPD updatedSkpd = skpdRepository.save(skpd);

        assertThat(updatedSkpd.getUpdatedBy()).isEqualTo(testUser.getId());
    }
}
