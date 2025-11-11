package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

public class CustomRepositoryFactory extends JpaRepositoryFactory {

    public CustomRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @NotNull
    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(
            RepositoryInformation information, @NotNull EntityManager entityManager) {

        JpaEntityInformation<?, Long> entityInformation = getEntityInformation(
                (Class<?>) information.getDomainType()
        );

        return new BaseRepositoryImpl<>(
                (JpaEntityInformation<? extends AbstractEntity, ?>) entityInformation,
                entityManager);
    }

    @NotNull
    @Override
    protected Class<?> getRepositoryBaseClass(@NotNull RepositoryMetadata metadata) {
        return BaseRepositoryImpl.class;
    }
}

