package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import org.springframework.data.jpa.domain.Specification;

public class ClusterFilter extends AbstractFilter<ClusterEntity> {

    private CompetitionEntity competition;

    @Override
    public Specification<ClusterEntity> getSpecification() {
        Specification<ClusterEntity> spec =  super.getSpecification();

        if (competition != null) {
            spec = spec.and(isOfCompetition(competition));
        }

        return spec;
    }

    public Specification<ClusterEntity> isOfCompetition(CompetitionEntity competition) {
        return (root, cq, cb) -> cb.equal(root.get("competition"), competition);
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
    }
}
