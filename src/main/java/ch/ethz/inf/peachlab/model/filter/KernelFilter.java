package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import org.springframework.data.jpa.domain.Specification;

public class KernelFilter extends AbstractFilter<KernelEntity> {

    private CompetitionEntity competition;

    @Override
    public Specification<KernelEntity> getSpecification() {
        Specification<KernelEntity> spec = super.getSpecification();
        if (competition != null) {
            spec = spec.and(isOfCompetition(competition));
        }

        return spec;
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
    }

    public CompetitionEntity getCompetition() {
        return competition;
    }

    private Specification<KernelEntity> isOfCompetition(CompetitionEntity competition) {
        return (root, cq, cb) -> cb.equal(root.get("competition"), competition);
    }
}
