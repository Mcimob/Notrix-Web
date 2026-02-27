package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.HasCellData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.util.Set;

public abstract class AbstractKernelFilter<T extends HasKernelData<ID, ? extends HasCellData>, ID> extends AbstractFilter<T>{
    @Serial
    private static final long serialVersionUID = 2094289064670774659L;

    private Set<ID> ids;

    @Override
    public Specification<T> getSpecification() {
        Specification<T> spec = super.getSpecification();
        if (ids != null) {
            spec = spec.and(hasId(ids));
        }
        return spec;
    }
    public void setIds(Set<ID> ids) {
        this.ids = ids;
    }

    private Specification<T> hasId(Set<ID> ids) {
        return (root, cq, cb) -> root.get("id").in(ids);
    }
}
