package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

public class AbstractCompetitionFilter<T extends HasCompetitionData<ID, ?, ?>, ID> extends AbstractFilter<T, ID> {
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

    private Specification<T> matchesSearchString(String searchString) {
        return ((root, cq, cb) ->
            cb.or(
                cb.like(cb.lower(root.get("title")), "%" + searchString.toLowerCase() + "%"),
                cb.like(
                    cb.lower(root.joinSet("tags", JoinType.LEFT)),
                    "%" + searchString.toLowerCase() + "%")
            ));
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
