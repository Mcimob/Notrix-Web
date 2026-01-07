package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import org.springframework.data.jpa.domain.Specification;

public class CompetitionFilter extends AbstractFilter<CompetitionEntity> {

    private String searchString;

    @Override
    public Specification<CompetitionEntity> getSpecification() {
        Specification<CompetitionEntity> spec = super.getSpecification();

        if (searchString != null) {
            spec = spec.and(matchesSearchString(searchString));
        }

        return spec;
    }

    Specification<CompetitionEntity> matchesSearchString(String searchString) {
        return ((root, cq, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + searchString.toLowerCase() + "%"));
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
