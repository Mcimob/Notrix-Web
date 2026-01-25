package ch.ethz.inf.peachlab.backend.service;

import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import ch.ethz.inf.peachlab.model.filter.AbstractFilter;
import ch.ethz.inf.peachlab.model.loadtype.HasLoadType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.CompletableFuture;

public interface BaseService<T extends AbstractEntity, F extends AbstractFilter<T>> extends HasLogger {

    ServiceResponse<PageImpl<T>> fetch(Pageable pageable, F filter, HasLoadType loadType);

    ServiceResponse<PageImpl<T>> fetch(Pageable pageable, F filter);

    ServiceResponse<T> fetchOne(F filter, HasLoadType loadType);

    ServiceResponse<T> fetchOne(F filter);

    ServiceResponse<T> fetchById(Long id, HasLoadType loadType);

    ServiceResponse<T> fetchById(Long id);

    ServiceResponse<Boolean> exists(F filter);

    ServiceResponse<Boolean> existsById(Long id);

    ServiceResponse<Long> count(F filter);

    ServiceResponse<T> save(T entity);

    ServiceResponse<PageImpl<T>> saveAll(Iterable<T> entities);

    ServiceResponse<T> delete(T entity);

    ServiceResponse<T> deleteById(Long id);

    ServiceResponse<T> deleteAll(Iterable<T> entities);

}
