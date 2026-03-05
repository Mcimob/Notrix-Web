package ch.ethz.inf.peachlab.backend.dao.db.impl;

import ch.ethz.inf.peachlab.backend.dao.db.BaseDao;
import ch.ethz.inf.peachlab.backend.dao.DaoException;
import ch.ethz.inf.peachlab.backend.dao.db.exception.VersioningDaoException;
import ch.ethz.inf.peachlab.backend.repository.BaseRepository;
import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import ch.ethz.inf.peachlab.model.filter.AbstractFilter;
import ch.ethz.inf.peachlab.model.loadtype.BaseLoadType;
import ch.ethz.inf.peachlab.model.loadtype.HasLoadType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class BaseDaoImpl<T extends AbstractEntity<ID>, F extends AbstractFilter<T, ID>, ID> implements BaseDao<T, F, ID> {

    private final BaseRepository<T, F, ID> repository;
    public BaseDaoImpl(BaseRepository<T, F, ID> repository) {
        this.repository = repository;
    }

    @Override
    public PageImpl<T> fetch(Pageable pageable, F filter, HasLoadType loadType) throws DaoException {
        try {
            return repository.findAll(filter, pageable, loadType);
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error fetching entities", e);
        }
    }

    @Override
    public PageImpl<T> fetch(Pageable pageable, F filter) throws DaoException {
        return fetch(pageable, filter, BaseLoadType.NONE);
    }

    @Override
    public Optional<T> fetchOne(F filter, HasLoadType loadType) throws DaoException {
        try {
            return repository.findOne(filter, loadType);
        } catch (EmptyResultDataAccessException | NoResultException e) {
            return Optional.empty();
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error fetching single entity", e);
        }
    }

    @Override
    public Optional<T> fetchOne(F filter) throws DaoException {
        return fetchOne(filter, BaseLoadType.NONE);
    }

    @Override
    public Optional<T> fetchById(ID id, HasLoadType loadType) throws DaoException {
        try {
            return repository.findById(id, loadType);
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error fetching entity by ID", e);
        }
    }

    @Override
    public Optional<T> fetchById(ID id) throws DaoException {
        return fetchById(id, BaseLoadType.NONE);
    }

    @Override
    public boolean exists(F filter) throws DaoException {
        try {
            return repository.exists(filter.getSpecification());
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error checking entity existence by filter", e);
        }
    }


    @Override
    public boolean existsById(ID id) throws DaoException {
        try {
            return repository.existsById(id);
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error checking entity existence by id", e);
        }
    }

    @Override
    public long count(F filter) throws DaoException {
        try {
            return repository.count(filter.getSpecification());
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error counting entities", e);
        }
    }

    @Override
    public Optional<T> save(T entity) throws DaoException {
        try {
            return Optional.of(repository.save(entity));
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            throw new VersioningDaoException("Optimistic locking failed", e);
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error saving entity", e);
        }
    }

    @Override
    public PageImpl<T> saveAll(Iterable<T> entities) throws DaoException {
        try {
            return new PageImpl<>(repository.saveAll(entities));
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            throw new VersioningDaoException("Optimistic locking failed", e);
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error saving entities", e);
        }
    }

    @Override
    public void delete(T entity) throws DaoException {
        try {
            repository.delete(entity);
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error deleting entity", e);
        }
    }

    @Override
    public void deleteById(ID id) throws DaoException {
        try {
            repository.deleteById(id);
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error deleting entity by ID", e);
        }
    }

    @Override
    public void deleteAll(Iterable<T> entities) throws DaoException {
        try {
            repository.deleteAll(entities);
        } catch (DataAccessException | PersistenceException e) {
            throw new DaoException("Error deleting entities", e);
        }
    }
}
