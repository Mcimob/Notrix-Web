package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import ch.ethz.inf.peachlab.model.filter.AbstractFilter;
import ch.ethz.inf.peachlab.model.loadtype.HasLoadType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends AbstractEntity<ID>, F extends AbstractFilter<T, ID>, ID>
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    Optional<T> findOne(F filter, HasLoadType loadType);

    PageImpl<T> findAll(F filter, Pageable pageable, HasLoadType loadType);

    Optional<T> findById(ID id, HasLoadType loadType);
}
