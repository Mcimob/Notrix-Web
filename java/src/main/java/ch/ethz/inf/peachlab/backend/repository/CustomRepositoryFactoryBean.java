package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import ch.ethz.inf.peachlab.model.filter.AbstractFilter;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

public class CustomRepositoryFactoryBean<
        R extends BaseRepository<T, F, ID>,
        T extends AbstractEntity<ID>,
        F extends AbstractFilter<T, ID>,
        ID>
        extends JpaRepositoryFactoryBean<R, T, ID> {

    public CustomRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected JpaRepositoryFactory createRepositoryFactory(EntityManager entityManager) {
        return new CustomRepositoryFactory(entityManager);
    }
}
