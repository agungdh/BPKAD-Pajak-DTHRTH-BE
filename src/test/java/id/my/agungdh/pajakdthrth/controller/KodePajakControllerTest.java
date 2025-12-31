package id.my.agungdh.pajakdthrth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.agungdh.pajakdthrth.dto.KodePajakRequest;
import id.my.agungdh.pajakdthrth.dto.KodePajakResponse;
import id.my.agungdh.pajakdthrth.service.KodePajakService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class KodePajakControllerTest {

        private MockMvc mockMvc;

        private ObjectMapper objectMapper = new ObjectMapper();

        @Mock
        private KodePajakService service;

        @InjectMocks
        private KodePajakController controller;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        }

        @Test
        void create_Success() throws Exception {
                KodePajakRequest request = new KodePajakRequest("PPh 25");
                KodePajakResponse response = new KodePajakResponse(UUID.randomUUID().toString(), "PPh 25");

                when(service.create(any(KodePajakRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/kode-pajak")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.nama").value("PPh 25"))
                                .andExpect(jsonPath("$.kode").doesNotExist());
        }

        @Test
        void create_InvalidInput_ShouldReturnBadRequest() throws Exception {
                // Empty Nama
                KodePajakRequest request = new KodePajakRequest("");

                mockMvc.perform(post("/api/kode-pajak")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void create_Duplicate_ShouldReturnConflict() throws Exception {
                KodePajakRequest request = new KodePajakRequest("PPh 25");

                when(service.create(any(KodePajakRequest.class)))
                                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate"));

                mockMvc.perform(post("/api/kode-pajak")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict());
        }

        @Test
        void findAll_Success() throws Exception {
                KodePajakResponse response = new KodePajakResponse(UUID.randomUUID().toString(), "PPh 25");
                when(service.findAll()).thenReturn(List.of(response));

                mockMvc.perform(get("/api/kode-pajak"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1))
                                .andExpect(jsonPath("$[0].nama").value("PPh 25"));
        }

        @Test
        void findByUuid_Success() throws Exception {
                String uuid = UUID.randomUUID().toString();
                KodePajakResponse response = new KodePajakResponse(uuid, "PPh 25");

                when(service.findByUuid(uuid)).thenReturn(response);

                mockMvc.perform(get("/api/kode-pajak/{uuid}", uuid))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.uuid").value(uuid))
                                .andExpect(jsonPath("$.nama").value("PPh 25"));
        }

        @Test
        void findByUuid_NotFound_ShouldReturn404() throws Exception {
                String uuid = UUID.randomUUID().toString();
                when(service.findByUuid(uuid))
                                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));

                mockMvc.perform(get("/api/kode-pajak/{uuid}", uuid))
                                .andExpect(status().isNotFound());
        }

        @Test
        void update_Success() throws Exception {
                String uuid = UUID.randomUUID().toString();
                KodePajakRequest request = new KodePajakRequest("PPh 25 Updated");
                KodePajakResponse response = new KodePajakResponse(uuid, "PPh 25 Updated");

                when(service.update(eq(uuid), any(KodePajakRequest.class))).thenReturn(response);

                mockMvc.perform(put("/api/kode-pajak/{uuid}", uuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nama").value("PPh 25 Updated"));
        }

        @Test
        void update_InvalidInput_ShouldReturnBadRequest() throws Exception {
                String uuid = UUID.randomUUID().toString();
                // Empty Name
                KodePajakRequest request = new KodePajakRequest("");

                mockMvc.perform(put("/api/kode-pajak/{uuid}", uuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void update_NotFound_ShouldReturn404() throws Exception {
                String uuid = UUID.randomUUID().toString();
                KodePajakRequest request = new KodePajakRequest("PPh 25");

                when(service.update(eq(uuid), any(KodePajakRequest.class)))
                                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found"));

                mockMvc.perform(put("/api/kode-pajak/{uuid}", uuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void delete_Success() throws Exception {
                String uuid = UUID.randomUUID().toString();
                doNothing().when(service).delete(uuid);

                mockMvc.perform(delete("/api/kode-pajak/{uuid}", uuid))
                                .andExpect(status().isNoContent());
        }

        @Test
        void delete_NotFound_ShouldReturn404() throws Exception {
                String uuid = UUID.randomUUID().toString();
                doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                                .when(service).delete(uuid);

                mockMvc.perform(delete("/api/kode-pajak/{uuid}", uuid))
                                .andExpect(status().isNotFound());
        }
}
