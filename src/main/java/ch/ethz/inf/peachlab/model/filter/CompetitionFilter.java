package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

public class CompetitionFilter extends AbstractFilter<CompetitionEntity> {

    @Serial
    private static final long serialVersionUID = -5315770429422503325L;
    private String searchString;
    private String slug;

    @Override
    public Specification<CompetitionEntity> getSpecification() {
        Specification<CompetitionEntity> spec = super.getSpecification();

        if (searchString != null) {
            spec = spec.and(matchesSearchString(searchString));
        }

        if (slug != null) {
            spec = spec.and(matchesSlug(slug));
        }

        return spec;
    }

    private Specification<CompetitionEntity> matchesSearchString(String searchString) {
        return ((root, cq, cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("title")), "%" + searchString.toLowerCase() + "%"),
                        cb.like(
                                cb.lower(root.joinSet("tags", JoinType.LEFT)),
                                "%" + searchString.toLowerCase() + "%")
                ));
    }

    private Specification<CompetitionEntity> matchesSlug(String slug) {
        return ((root, cq, cb) ->
                cb.equal(root.get("slug"), slug));
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
