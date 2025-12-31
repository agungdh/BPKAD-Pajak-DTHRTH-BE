package id.my.agungdh.pajakdthrth.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.my.agungdh.pajakdthrth.BaseIntegrationTest;

public class KodePajakSecurityTest extends BaseIntegrationTest {

        @Autowired
        private WebApplicationContext context;

        @Test
        void accessKodePajakEndpoint_WithoutToken_ShouldReturnForbidden() throws Exception {
                MockMvc mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                mockMvc.perform(get("/api/kode-pajak"))
                                .andExpect(status().isForbidden());

                mockMvc.perform(post("/api/kode-pajak"))
                                .andExpect(status().isForbidden());
        }
}
