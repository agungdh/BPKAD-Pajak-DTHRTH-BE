package id.my.agungdh.pajakdthrth.repository;

import id.my.agungdh.pajakdthrth.model.Dth;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DthRepository extends SoftDeleteRepository<Dth, Long> {
    Optional<Dth> findByUuid(String uuid);
}
