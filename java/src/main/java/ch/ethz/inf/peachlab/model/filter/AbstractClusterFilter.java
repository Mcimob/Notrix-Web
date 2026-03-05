package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.HasClusterData;
import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

public class AbstractClusterFilter<T extends HasClusterData<K, C>, K extends HasKernelData<?, ?, ?>, C extends HasCompetitionData<?, K, ?>> extends AbstractFilter<T, Long> {
    @Serial
    private static final long serialVersionUID = -4518740573687773169L;

    private C competition;
    private Long localClusterId;

    @Override
    public Specification<T> getSpecification() {
        Specification<T> spec =  super.getSpecification();

        if (competition != null) {
            spec = spec.and(isOfCompetition(competition));
        }

        if (localClusterId != null) {
            spec = spec.and(hasLocalClusterId(localClusterId));
        }

        return spec;
    }

    public Specification<T> isOfCompetition(C competition) {
        return (root, cq, cb) -> cb.equal(root.get("competition"), competition);
    }

    public Specification<T> hasLocalClusterId(Long localClusterId) {
        return (root, cq, cb) -> cb.equal(root.get("localClusterId"), localClusterId);
    }

    public void setCompetition(C competition) {
        this.competition = competition;
    }

    public void setLocalClusterId(Long localClusterId) {
        this.localClusterId = localClusterId;
    }

    public static <T extends HasClusterData<K, C>, K extends HasKernelData<?, ?, C>, C extends HasCompetitionData<?, K, T>> AbstractClusterFilter<T, K, C> copyFilter(AbstractClusterFilter<T, K, C> filter) {
        AbstractClusterFilter<T, K, C> f = new AbstractClusterFilter<>();
        f.setCompetition(filter.competition);
        f.setIds(filter.ids);
        f.setLocalClusterId(filter.localClusterId);
        return f;
    }
}
