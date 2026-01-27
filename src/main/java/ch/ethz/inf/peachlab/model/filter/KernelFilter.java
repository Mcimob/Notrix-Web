package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import org.springframework.data.jpa.domain.Specification;

public class KernelFilter extends AbstractFilter<KernelEntity> {

    private Long id;
    private CompetitionEntity competition;
    private String user;
    private String slug;

    @Override
    public Specification<KernelEntity> getSpecification() {
        Specification<KernelEntity> spec = super.getSpecification();
        if (id != null) {
            return spec.and(hasId(id));
        }
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
    }

    public CompetitionEntity getCompetition() {
        return competition;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    private Specification<KernelEntity> hasId(Long id) {
        return (root, cq, cb) -> cb.equal(root.get("id"), id);
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
