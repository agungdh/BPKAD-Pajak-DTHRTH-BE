package id.my.agungdh.pajakdthrth.repository;

import id.my.agungdh.pajakdthrth.model.SKPD;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkpdRepository extends SoftDeleteRepository<SKPD, Long> {
    java.util.Optional<SKPD> findByNama(String nama);

    Optional<SKPD> findByUuid(String uuid);
}
