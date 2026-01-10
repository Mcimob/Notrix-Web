package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

public abstract class AbstractFilter<T extends AbstractEntity> implements Serializable {

    public Specification<T> getSpecification() {
        Specification<T> spec = Specification.unrestricted();
        spec = spec.and((root, cq, cb) -> {
            cq.distinct(true);
            return cb.conjunction();
        });
        return spec;
    }
}
