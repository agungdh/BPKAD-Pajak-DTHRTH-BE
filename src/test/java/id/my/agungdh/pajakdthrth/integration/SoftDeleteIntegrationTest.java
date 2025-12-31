package id.my.agungdh.pajakdthrth.integration;

import id.my.agungdh.pajakdthrth.BaseIntegrationTest;
import id.my.agungdh.pajakdthrth.model.KodePajak;
import id.my.agungdh.pajakdthrth.repository.KodePajakRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SoftDeleteIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private KodePajakRepository kodePajakRepository;

    @Test
    void shouldAllowCreatingDuplicateAfterSoftDelete() {
        // 1. Create active record
        String nama = "Pajak Unique Test " + java.util.UUID.randomUUID();
        KodePajak kp1 = new KodePajak();
        kp1.setNama(nama);
        kodePajakRepository.save(kp1);
        assertThat(kp1.getId()).isNotNull();

        // 2. Soft delete
        kodePajakRepository.delete(kp1);
        kodePajakRepository.flush();

        // Verify it's gone from normal queries
        Optional<KodePajak> deletedKp = kodePajakRepository.findById(kp1.getId());
        assertThat(deletedKp).isEmpty();

        // 3. Create duplicate record with same name
        KodePajak kp2 = new KodePajak();
        kp2.setNama(nama);
        KodePajak savedKp2 = kodePajakRepository.save(kp2);

        assertThat(savedKp2.getId()).isNotNull();
        assertThat(savedKp2.getId()).isNotEqualTo(kp1.getId());
        assertThat(savedKp2.getNama()).isEqualTo(nama);
    }
}
