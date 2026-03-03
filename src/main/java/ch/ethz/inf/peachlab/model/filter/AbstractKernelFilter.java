package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.HasCellData;
import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.util.Set;

public abstract class AbstractKernelFilter<T extends HasKernelData<ID, ? extends HasCellData, CO>, ID, CO extends HasCompetitionData<ID, ?, ?>> extends AbstractFilter<T, ID>{
    @Serial
    private static final long serialVersionUID = 2094289064670774659L;

    private CO competition;
    private Set<ID> competitionIds;

    @Override
    public Specification<T> getSpecification() {
        Specification<T> spec =  super.getSpecification();

        if (competitionIds != null) {
            spec = spec.and(isOfCompetition(competitionIds));
        }

        if (competition != null) {
            spec = spec.and(isOfCompetition(competition));
        }

        return spec;
    }

    private Specification<T> isOfCompetition(Set<ID> competitionIds) {
        return (root, cq, cb) -> root.get("competition").get("id").in(competitionIds);
    }

    private Specification<T> isOfCompetition(CO competition) {
        return isOfCompetition(Set.of(competition.getId()));
    }

    public void setCompetition(CO competition) {
        this.competition = competition;
    }

    public void setCompetitionIds(Set<ID> competitionIds) {
        this.competitionIds = competitionIds;
    }
}
