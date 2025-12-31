package id.my.agungdh.pajakdthrth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SoftDeleteRepository<T, ID> extends JpaRepository<T, ID> {
    // Standard JpaRepository methods are sufficient, we'll override the
    // implementation
}
