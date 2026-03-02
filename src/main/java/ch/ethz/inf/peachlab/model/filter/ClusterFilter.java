package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

public class ClusterFilter extends AbstractFilter<ClusterEntity, Long> {

    @Serial
    private static final long serialVersionUID = 5973223330744160086L;
    private CompetitionEntity competition;
    private Long localClusterId;

    @Override
    public Specification<ClusterEntity> getSpecification() {
        Specification<ClusterEntity> spec =  super.getSpecification();

        if (competition != null) {
            spec = spec.and(isOfCompetition(competition));
        }

        if (localClusterId != null) {
            spec = spec.and(hasLocalClusterId(localClusterId));
        }

        return spec;
    }

    public Specification<ClusterEntity> isOfCompetition(CompetitionEntity competition) {
        return (root, cq, cb) -> cb.equal(root.get("competition"), competition);
    }

    public Specification<ClusterEntity> hasLocalClusterId(Long localClusterId) {
        return (root, cq, cb) -> cb.equal(root.get("localClusterId"), localClusterId);
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
    }

    public void setLocalClusterId(Long localClusterId) {
        this.localClusterId = localClusterId;
    }
}
