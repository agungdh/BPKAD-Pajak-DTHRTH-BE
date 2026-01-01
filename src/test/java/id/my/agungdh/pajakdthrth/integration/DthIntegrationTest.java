package id.my.agungdh.pajakdthrth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.agungdh.pajakdthrth.BaseIntegrationTest;
import id.my.agungdh.pajakdthrth.dto.DthRequest;
import id.my.agungdh.pajakdthrth.model.Dth;
import id.my.agungdh.pajakdthrth.model.Role;
import id.my.agungdh.pajakdthrth.model.SKPD;
import id.my.agungdh.pajakdthrth.model.User;
import id.my.agungdh.pajakdthrth.repository.DthRepository;
import id.my.agungdh.pajakdthrth.repository.SkpdRepository;
import id.my.agungdh.pajakdthrth.repository.UserRepository;
import id.my.agungdh.pajakdthrth.security.RedisTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private DthRepository dthRepository;

    @Autowired
    private SkpdRepository skpdRepository;

    @Autowired
    private UserRepository userRepository;

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
    private Dth testDth;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);

        dthRepository.deleteAll();
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

        // Setup initial DTH Data
        testDth = new Dth();
        testDth.setNoSpm("SPM-001");
        testDth.setTglSpm(LocalDate.now());
        testDth.setNilaiBelanjaSpm(new BigDecimal("1000000"));
        testDth.setNoSp2d("SP2D-001");
        testDth.setTglSp2d(LocalDate.now());
        testDth.setNilaiBelanjaSp2d(new BigDecimal("1000000"));
        testDth.setKodeAkun("5.1.01");
        testDth.setPajakPpn(new BigDecimal("100000"));
        testDth.setJumlahPajak(new BigDecimal("100000"));
        testDth.setNamaRekanan("CV. Test");
        testDth.setSkpd(testSkpd);
        testDth = dthRepository.save(testDth);
    }

    @Test
    void create_AsUser_Success() throws Exception {
        DthRequest request = new DthRequest();
        request.setNoSpm("SPM-NEW");
        request.setTglSpm(LocalDate.now());
        request.setNilaiBelanjaSpm(new BigDecimal("5000000"));
        request.setNoSp2d("SP2D-NEW");
        request.setTglSp2d(LocalDate.now());
        request.setNilaiBelanjaSp2d(new BigDecimal("5000000"));
        request.setKodeAkun("5.2.02");
        request.setNamaRekanan("PT. Baru");
        request.setSkpdId(testSkpd.getUuid());
        request.setPajakPpn(new BigDecimal("500000"));
        request.setPajakPph21(new BigDecimal("100000"));

        mockMvc.perform(post("/api/dth")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.no_spm", is("SPM-NEW")))
                .andExpect(jsonPath("$.pajak_ppn", is(500000)))
                .andExpect(jsonPath("$.jumlah_pajak", is(600000))); // 500k + 100k
    }

    @Test
    void findAll_AsUser_Success() throws Exception {
        mockMvc.perform(get("/api/dth")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThanOrEqualTo(1)));
    }

    @Test
    void update_AsAdmin_Success() throws Exception {
        DthRequest request = new DthRequest();
        request.setNoSpm("SPM-UPDATED");
        request.setTglSpm(LocalDate.now());
        request.setNilaiBelanjaSpm(new BigDecimal("2000000"));
        request.setNoSp2d("SP2D-UPDATED");
        request.setTglSp2d(LocalDate.now());
        request.setNilaiBelanjaSp2d(new BigDecimal("2000000"));
        request.setKodeAkun("5.1.01");
        request.setNamaRekanan("CV. Test");
        request.setSkpdId(testSkpd.getUuid());

        mockMvc.perform(put("/api/dth/" + testDth.getUuid())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.no_spm", is("SPM-UPDATED")));
    }

    @Test
    void update_AsUser_Forbidden() throws Exception {
        DthRequest request = new DthRequest();
        request.setNoSpm("SPM-FAIL");
        // ... omitted other fields but it doesn't matter for 403 check
        request.setTglSpm(LocalDate.now());
        request.setNilaiBelanjaSpm(BigDecimal.ZERO);
        request.setNoSp2d("SP");
        request.setTglSp2d(LocalDate.now());
        request.setNilaiBelanjaSp2d(BigDecimal.ZERO);
        request.setKodeAkun("1");
        request.setNamaRekanan("A");
        request.setSkpdId(testSkpd.getUuid());

        mockMvc.perform(put("/api/dth/" + testDth.getUuid())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_AsAdmin_Success() throws Exception {
        mockMvc.perform(delete("/api/dth/" + testDth.getUuid())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/dth/" + testDth.getUuid())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_AsUser_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/dth/" + testDth.getUuid())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
