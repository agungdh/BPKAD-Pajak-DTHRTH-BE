package id.my.agungdh.pajakdthrth.repository;

import id.my.agungdh.pajakdthrth.model.SKPD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkpdRepository extends JpaRepository<SKPD, Long> {
    Optional<SKPD> findByUuid(String uuid);
}
