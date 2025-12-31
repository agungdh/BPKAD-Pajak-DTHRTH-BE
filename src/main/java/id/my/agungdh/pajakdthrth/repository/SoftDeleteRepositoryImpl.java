package id.my.agungdh.pajakdthrth.repository;

import id.my.agungdh.pajakdthrth.model.BaseEntity;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Optional;

public class SoftDeleteRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements SoftDeleteRepository<T, ID> {

    private final EntityManager entityManager;

    public SoftDeleteRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    public SoftDeleteRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }

    @Override
    @Transactional
    public void delete(T entity) {
        Assert.notNull(entity, "Entity must not be null");

        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setDeletedAt(LocalDateTime.now());

            AuditorAware<Long> auditorAware = id.my.agungdh.pajakdthrth.util.SpringContext.getBean(AuditorAware.class);
            if (auditorAware != null) {
                Optional<Long> currentAuditor = auditorAware.getCurrentAuditor();
                currentAuditor.ifPresent(baseEntity::setDeletedBy);
            }

            super.save(entity);
        } else {
            super.delete(entity);
        }
    }

    // You might also want to override deleteById etc., but SimpleJpaRepository's
    // deleteById calls delete(T entity),
    // provided the entity is found.
    // However, deleteById(ID id) implementation in SimpleJpaRepository fetches the
    // entity and calls delete(entity).
    // So modifying delete(T entity) is usually enough for single deletes.

    // BUT, deleteAll(), deleteAllInBatch(), etc. need careful handling if used.
    // For now, let's focus on the primary delete method used by the app.
}
