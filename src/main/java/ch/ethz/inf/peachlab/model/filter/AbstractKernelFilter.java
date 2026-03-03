package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.HasCellData;
import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

public abstract class AbstractKernelFilter<T extends HasKernelData<ID, ? extends HasCellData, CO>, ID, CO extends HasCompetitionData<?, ?, ?>> extends AbstractFilter<T, ID>{
    @Serial
    private static final long serialVersionUID = 2094289064670774659L;

    private CO competition;

    @Override
    public Specification<T> getSpecification() {
        Specification<T> spec =  super.getSpecification();

        if (competition != null) {
            spec = spec.and(isOfCompetition(competition));
        }

        return spec;
    }

    private Specification<T> isOfCompetition(CO competition) {
        return (root, cq, cb) -> cb.equal(root.get("competition"), competition);
    }

    public void setCompetition(CO competition) {
        this.competition = competition;
    }
}
