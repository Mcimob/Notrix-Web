package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import ch.ethz.inf.peachlab.model.filter.AbstractFilter;
import ch.ethz.inf.peachlab.model.loadtype.HasLoadType;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public class BaseRepositoryImpl<T extends AbstractEntity, F extends AbstractFilter<T>>
        extends SimpleJpaRepository<T, Long> implements BaseRepository<T, F> {

    private final EntityManager em;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
    }

    @Override
    public Optional<T> findOne(F filter, HasLoadType loadType) {
        Specification<T> specification = filter.getSpecification();
        TypedQuery<T> query = createQueryWithEntityGraph(specification, loadType.getName(), Sort.unsorted());
        T result = query.getSingleResult();
        return Optional.ofNullable(result);
    }

    public Optional<T> findById(Long id, HasLoadType loadType) {
        Specification<T> specification = (root, query, cb) -> cb.equal(root.get("id"), id);
        TypedQuery<T> query = createQueryWithEntityGraph(specification, loadType.getName(), Sort.unsorted());
        T result = query.getSingleResult();
        return Optional.ofNullable(result);
    }

    @Override
    public Page<T> findAll(F filter, Pageable pageable, HasLoadType loadType) {
        Specification<T> specification = filter.getSpecification();

        TypedQuery<T> query = createQueryWithEntityGraph(specification, loadType.getName(), pageable.getSort());

        if (!pageable.isUnpaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List<T> result = query.getResultList();
        long totalRows = count(filter.getSpecification());

        return new PageImpl<>(result, pageable, totalRows);
    }

    private TypedQuery<T> createQueryWithEntityGraph(
            Specification<T> specification, String entityGraphName, Sort sort) {
        TypedQuery<T> query = getQuery(specification, sort);
        if (entityGraphName != null) {
            EntityGraph<?> entityGraph = em.getEntityGraph(entityGraphName);
            return query.setHint("javax.persistence.loadgraph", entityGraph);
        }
        return query;
    }
}
