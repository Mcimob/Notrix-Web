package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

public abstract class AbstractFilter<T extends AbstractEntity> implements Serializable {

    public Specification<T> getSpecification() {
        Specification<T> spec = Specification.unrestricted();
        return spec;
    }
}
