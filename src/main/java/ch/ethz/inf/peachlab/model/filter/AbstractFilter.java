package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.AbstractEntity;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public abstract class AbstractFilter<T extends AbstractEntity<ID>, ID> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1918280844513433777L;
    private Set<ID> ids;

    public Specification<T> getSpecification() {
        Specification<T> spec = Specification.unrestricted();

        if (ids != null) {
            spec = spec.and(hasId(ids));
        }

        spec = spec.and((root, cq, cb) -> {
            cq.distinct(true);
            return cb.conjunction();
        });
        return spec;
    }

    private Specification<T> hasId(Set<ID> ids) {
        return (root, cq, cb) -> root.get("id").in(ids);
    }

    public void setIds(Set<ID> ids) {
        this.ids = ids;
    }
}
