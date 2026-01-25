package ch.ethz.inf.peachlab.backend.dao;

import ch.ethz.inf.peachlab.backend.dao.exception.DaoException;
import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import ch.ethz.inf.peachlab.model.filter.AbstractFilter;
import ch.ethz.inf.peachlab.model.loadtype.HasLoadType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BaseDao<T extends AbstractEntity, F extends AbstractFilter<T>> {

    PageImpl<T> fetch(Pageable pageable, F filter, HasLoadType loadType) throws DaoException;

    PageImpl<T> fetch(Pageable pageable, F filter) throws DaoException;

    Optional<T> fetchOne(F filter, HasLoadType loadType) throws DaoException;

    Optional<T> fetchOne(F filter) throws DaoException;

    Optional<T> fetchById(Long id, HasLoadType loadType) throws DaoException;

    Optional<T> fetchById(Long id) throws DaoException;

    boolean exists(F filter) throws DaoException;

    boolean existsById(Long id) throws DaoException;

    long count(F filter) throws DaoException;

    Optional<T> save(T entity) throws DaoException;

    PageImpl<T> saveAll(Iterable<T> entities) throws DaoException;

    void delete(T entity) throws DaoException;

    void deleteById(Long id) throws DaoException;

    void deleteAll(Iterable<T> entities) throws DaoException;
}
