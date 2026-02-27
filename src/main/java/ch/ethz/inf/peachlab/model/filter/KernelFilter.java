package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

public class KernelFilter extends AbstractKernelFilter<KernelEntity, Long> {

    @Serial
    private static final long serialVersionUID = -1481439654987750735L;

    private CompetitionEntity competition;
    private String user;
    private String slug;

    @Override
    public Specification<KernelEntity> getSpecification() {
        Specification<KernelEntity> spec = super.getSpecification();
        if (competition != null) {
            spec = spec.and(isOfCompetition(competition));
        }

        if (user != null) {
            spec = spec.and(isFromUser(user));
        }

        if (slug != null) {
            spec = spec.and(hasSlug(slug));
        }

        return spec;
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    private Specification<KernelEntity> isOfCompetition(CompetitionEntity competition) {
        return (root, cq, cb) -> cb.equal(root.get("competition"), competition);
    }

    private Specification<KernelEntity> isFromUser(String user) {
        return (root, cq, cb) -> cb.equal(root.get("authorUserName"), user);
    }

    private Specification<KernelEntity> hasSlug(String slug) {
        return (root, cq, cb) -> cb.equal(root.get("currentUrlSlug"), slug);
    }
}
