package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

public class UploadedCompetitionFilter extends AbstractCompetitionFilter<UploadedCompetitionEntity, String> {
    @Serial
    private static final long serialVersionUID = -6314229679545377604L;

    @Override
    protected Specification<UploadedCompetitionEntity> matchesSearchString(String searchString) {
        return ((root, cq, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + searchString.toLowerCase() + "%")
            );
    }
}
