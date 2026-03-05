package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

public abstract class AbstractCompetitionFilter<T extends HasCompetitionData<ID, ?, ?>, ID> extends AbstractFilter<T, ID> {
    @Serial
    private static final long serialVersionUID = 9216174540362903242L;
    private String searchString;

    @Override
    public Specification<T> getSpecification() {
        Specification<T> spec = super.getSpecification();

        if (searchString != null) {
            spec = spec.and(matchesSearchString(searchString));
        }

        return spec;
    }

    protected abstract Specification<T> matchesSearchString(String searchString);

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
