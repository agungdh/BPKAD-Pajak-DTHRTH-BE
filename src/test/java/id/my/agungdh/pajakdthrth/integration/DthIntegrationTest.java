package id.my.agungdh.pajakdthrth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.agungdh.pajakdthrth.BaseIntegrationTest;
import id.my.agungdh.pajakdthrth.dto.DthRequest;
import id.my.agungdh.pajakdthrth.model.Dth;
import id.my.agungdh.pajakdthrth.model.KodePajak;
import id.my.agungdh.pajakdthrth.model.Role;
import id.my.agungdh.pajakdthrth.model.SKPD;
import id.my.agungdh.pajakdthrth.model.User;
import id.my.agungdh.pajakdthrth.repository.DthRepository;
import id.my.agungdh.pajakdthrth.repository.KodePajakRepository;
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
    private KodePajakRepository kodePajakRepository;

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
    private KodePajak testKodePajak;
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
        kodePajakRepository.deleteAll();

        // Create SKPD
        testSkpd = new SKPD();
        testSkpd.setNama("Dinas Test");
        testSkpd = skpdRepository.saveAndFlush(testSkpd);

        // Create Kode Pajak
        testKodePajak = new KodePajak();
        testKodePajak.setNama("411122 - PPh Pasal 22");
        testKodePajak = kodePajakRepository.saveAndFlush(testKodePajak);

        // Create Admin
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("password"));
        adminUser.setNama("Admin User");
        adminUser.setRole(Role.ADMIN);
        adminUser = userRepository.saveAndFlush(adminUser);
        adminToken = redisTokenService.createToken(adminUser.getId());

        // Create Regular User
        regularUser = new User();
        regularUser.setUsername("user");
        regularUser.setPassword(passwordEncoder.encode("password"));
        regularUser.setNama("Regular User");
        regularUser.setRole(Role.USER);
        regularUser.setSkpd(testSkpd);
        regularUser = userRepository.saveAndFlush(regularUser);
        userToken = redisTokenService.createToken(regularUser.getId());

        // Setup initial DTH Data
        testDth = new Dth();
        testDth.setNoSpm("SPM-001");
        testDth.setTglSpm(LocalDate.now());
        testDth.setNilaiBelanjaSpm(new BigDecimal("1000000"));
        testDth.setNoSp2d("SP2D-001");
        testDth.setTglSp2d(LocalDate.now());
        testDth.setNilaiBelanjaSp2d(new BigDecimal("1000000"));
        testDth.setKodePajak(testKodePajak);
        testDth.setJumlahPajak(new BigDecimal("100000"));
        testDth.setNamaRekanan("CV. Test");
        testDth.setSkpd(testSkpd);
        testDth = dthRepository.saveAndFlush(testDth);
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
        request.setKodePajakId(testKodePajak.getUuid());
        request.setNamaRekanan("PT. Baru");
        // request.setSkpdId(testSkpd.getUuid()); // User request should not need SKPD
        // ID
        request.setJumlahPajak(new BigDecimal("600000"));

        // Verify token is valid
        java.util.Optional<Long> userIdOpt = redisTokenService.validateToken(userToken);
        if (userIdOpt.isEmpty()) {
            throw new RuntimeException("Token validation failed in test setup! Token: " + userToken);
        } else {
            System.out.println("Token valid for user ID: " + userIdOpt.get());
        }

        mockMvc.perform(post("/api/dth")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.no_spm", is("SPM-NEW")))
                .andExpect(jsonPath("$.jumlah_pajak", is(600000)))
                .andExpect(jsonPath("$.skpd.uuid", is(testSkpd.getUuid()))); // Verify assigned to User's SKPD
    }

    @Test
    void findAll_AsUser_FiltersBySkpd() throws Exception {
        // Create another SKPD and DTH
        SKPD otherSkpd = new SKPD();
        otherSkpd.setNama("Dinas Lain");
        otherSkpd = skpdRepository.saveAndFlush(otherSkpd);

        Dth otherDth = new Dth();
        otherDth.setNoSpm("SPM-OTHER");
        otherDth.setTglSpm(LocalDate.now());
        otherDth.setNilaiBelanjaSpm(new BigDecimal("1000000"));
        otherDth.setNoSp2d("SP2D-OTHER");
        otherDth.setTglSp2d(LocalDate.now());
        otherDth.setNilaiBelanjaSp2d(new BigDecimal("1000000"));
        otherDth.setKodePajak(testKodePajak);
        otherDth.setJumlahPajak(new BigDecimal("100000"));
        otherDth.setNamaRekanan("CV. Other");
        otherDth.setSkpd(otherSkpd);
        dthRepository.saveAndFlush(otherDth);

        // User (linked to testSkpd) should NOT see otherDth
        mockMvc.perform(get("/api/dth")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].uuid", hasItem(testDth.getUuid())))
                .andExpect(jsonPath("$.[*].uuid", not(hasItem(otherDth.getUuid()))));

        // Admin should see BOTH
        mockMvc.perform(get("/api/dth")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].uuid", hasItem(testDth.getUuid())))
                .andExpect(jsonPath("$.[*].uuid", hasItem(otherDth.getUuid())));
    }

    @Test
    void access_MasterData_AsUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/skpd")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/kode-pajak")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
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
        request.setKodePajakId(testKodePajak.getUuid());
        request.setJumlahPajak(new BigDecimal("200000"));
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
        request.setTglSpm(LocalDate.now());
        request.setNilaiBelanjaSpm(BigDecimal.ZERO);
        request.setNoSp2d("SP");
        request.setTglSp2d(LocalDate.now());
        request.setNilaiBelanjaSp2d(BigDecimal.ZERO);
        request.setKodePajakId(testKodePajak.getUuid());
        request.setJumlahPajak(BigDecimal.ZERO);
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

    @Test
    void exportPdf_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/dth/export/pdf")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(
                        header().string("Content-Disposition", containsString("attachment; filename=dth_report.pdf")));
    }
}
