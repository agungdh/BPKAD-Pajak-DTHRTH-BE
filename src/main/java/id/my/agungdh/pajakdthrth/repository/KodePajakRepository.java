package id.my.agungdh.pajakdthrth.repository;

import id.my.agungdh.pajakdthrth.model.KodePajak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KodePajakRepository extends JpaRepository<KodePajak, Long> {
    Optional<KodePajak> findByNama(String nama);

    Optional<KodePajak> findByUuid(String uuid);

    boolean existsByNama(String nama);
}
