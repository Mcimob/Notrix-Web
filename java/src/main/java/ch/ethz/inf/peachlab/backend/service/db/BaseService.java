package ch.ethz.inf.peachlab.backend.service.db;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import ch.ethz.inf.peachlab.model.filter.AbstractFilter;
import ch.ethz.inf.peachlab.model.loadtype.HasLoadType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface BaseService<T extends AbstractEntity<ID>, F extends AbstractFilter<T, ID>, ID> extends HasLogger {

    ServiceResponse<PageImpl<T>> fetch(Pageable pageable, F filter, HasLoadType loadType);

    ServiceResponse<PageImpl<T>> fetch(Pageable pageable, F filter);

    ServiceResponse<T> fetchOne(F filter, HasLoadType loadType);

    ServiceResponse<T> fetchOne(F filter);

    ServiceResponse<T> fetchById(ID id, HasLoadType loadType);

    ServiceResponse<T> fetchById(ID id);

    ServiceResponse<Boolean> exists(F filter);

    ServiceResponse<Boolean> existsById(ID id);

    ServiceResponse<Long> count(F filter);

    ServiceResponse<T> save(T entity);

    ServiceResponse<PageImpl<T>> saveAll(Iterable<T> entities);

    ServiceResponse<T> delete(T entity);

    ServiceResponse<T> deleteById(ID id);

    ServiceResponse<T> deleteAll(Iterable<T> entities);

}
