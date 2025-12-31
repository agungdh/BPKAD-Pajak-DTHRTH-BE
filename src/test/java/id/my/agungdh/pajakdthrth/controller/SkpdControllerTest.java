package id.my.agungdh.pajakdthrth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import id.my.agungdh.pajakdthrth.dto.SkpdRequest;
import id.my.agungdh.pajakdthrth.dto.SkpdResponse;
import id.my.agungdh.pajakdthrth.service.SkpdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SkpdControllerTest {

        private MockMvc mockMvc;

        private ObjectMapper objectMapper;

        @Mock
        private SkpdService skpdService;

        @InjectMocks
        private SkpdController skpdController;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

                org.springframework.http.converter.json.MappingJackson2HttpMessageConverter converter = new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter();
                converter.setObjectMapper(objectMapper);

                mockMvc = MockMvcBuilders.standaloneSetup(skpdController)
                                .setMessageConverters(converter)
                                .build();
        }

        // --- POSITIVE CASES ---

        @Test
        void createSkpd_Success() throws Exception {
                SkpdRequest request = new SkpdRequest("Dinas Pendapatan");
                SkpdResponse response = new SkpdResponse(UUID.randomUUID().toString(), "Dinas Pendapatan");

                when(skpdService.create(any(SkpdRequest.class))).thenReturn(response);

                String jsonRequest = objectMapper.writeValueAsString(request);

                mockMvc.perform(post("/api/skpd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.uuid").value(response.uuid()))
                                .andExpect(jsonPath("$.nama").value(response.nama()));
        }

        @Test
        void findAll_Success() throws Exception {
                SkpdResponse response = new SkpdResponse(UUID.randomUUID().toString(), "Dinas Pendapatan");
                when(skpdService.findAll()).thenReturn(List.of(response));

                mockMvc.perform(get("/api/skpd"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1))
                                .andExpect(jsonPath("$[0].nama").value("Dinas Pendapatan"));
        }

        @Test
        void findByUuid_Success() throws Exception {
                String uuid = UUID.randomUUID().toString();
                SkpdResponse response = new SkpdResponse(uuid, "Dinas Pendapatan");

                when(skpdService.findByUuid(uuid)).thenReturn(response);

                mockMvc.perform(get("/api/skpd/{uuid}", uuid))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.uuid").value(uuid))
                                .andExpect(jsonPath("$.nama").value("Dinas Pendapatan"));
        }

        @Test
        void updateSkpd_Success() throws Exception {
                String uuid = UUID.randomUUID().toString();
                SkpdRequest request = new SkpdRequest("Dinas Kehutanan");
                SkpdResponse response = new SkpdResponse(uuid, "Dinas Kehutanan");

                when(skpdService.update(eq(uuid), any(SkpdRequest.class))).thenReturn(response);

                mockMvc.perform(put("/api/skpd/{uuid}", uuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nama").value("Dinas Kehutanan"));
        }

        @Test
        void deleteSkpd_Success() throws Exception {
                String uuid = UUID.randomUUID().toString();
                mockMvc.perform(delete("/api/skpd/{uuid}", uuid))
                                .andExpect(status().isNoContent());
        }

        // --- NEGATIVE CASES (VALIDATION & ERROR HANDLING) ---

        @Test
        void createSkpd_Invalid_BlankName() throws Exception {
                SkpdRequest invalidRequest = new SkpdRequest("");

                mockMvc.perform(post("/api/skpd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void createSkpd_Invalid_NullName() throws Exception {
                SkpdRequest invalidRequest = new SkpdRequest(null);

                mockMvc.perform(post("/api/skpd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void findByUuid_NotFound() throws Exception {
                String uuid = UUID.randomUUID().toString();
                when(skpdService.findByUuid(uuid))
                                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "SKPD not found"));

                mockMvc.perform(get("/api/skpd/{uuid}", uuid))
                                .andExpect(status().isNotFound());
        }

        @Test
        void updateSkpd_NotFound() throws Exception {
                String uuid = UUID.randomUUID().toString();
                SkpdRequest request = new SkpdRequest("Dispenda");

                when(skpdService.update(eq(uuid), any(SkpdRequest.class)))
                                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "SKPD not found"));

                mockMvc.perform(put("/api/skpd/{uuid}", uuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }
}
