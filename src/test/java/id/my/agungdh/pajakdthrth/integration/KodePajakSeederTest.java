package id.my.agungdh.pajakdthrth.integration;

import id.my.agungdh.pajakdthrth.model.KodePajak;
import id.my.agungdh.pajakdthrth.repository.KodePajakRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import id.my.agungdh.pajakdthrth.BaseIntegrationTest;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "test", "seeding" })
public class KodePajakSeederTest extends BaseIntegrationTest {

    @Autowired
    private KodePajakRepository kodePajakRepository;

    @Test
    void seededDataShouldExist() {
        // PPN
        Optional<KodePajak> ppn = kodePajakRepository.findByNama("411211 PPN");
        assertThat(ppn).isPresent();

        // PPh 21
        Optional<KodePajak> pph21 = kodePajakRepository.findByNama("411121 PPh 21");
        assertThat(pph21).isPresent();

        // PPh 4/2
        Optional<KodePajak> pph42 = kodePajakRepository.findByNama("PPh 4/2 PPh 4/2");
        assertThat(pph42).isPresent();

        // Check count
        assertThat(kodePajakRepository.count()).isGreaterThanOrEqualTo(5);
    }
}
