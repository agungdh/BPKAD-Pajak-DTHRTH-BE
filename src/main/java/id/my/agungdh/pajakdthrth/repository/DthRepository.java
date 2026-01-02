package id.my.agungdh.pajakdthrth.repository;

import id.my.agungdh.pajakdthrth.model.Dth;
import id.my.agungdh.pajakdthrth.model.SKPD;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DthRepository extends SoftDeleteRepository<Dth, Long> {
    Optional<Dth> findByUuid(String uuid);

    List<Dth> findBySkpd(SKPD skpd);
}
