package ch.ethz.inf.peachlab.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import java.io.Serial;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class CompetitionEntity extends HasCompetitionData<Long, KernelEntity, ClusterEntity> {

    @Serial
    private static final long serialVersionUID = 2263782821633158150L;

    @Id
    private Long id;

    @ElementCollection
    @CollectionTable(name = "CompetitionTags", joinColumns = {@JoinColumn(name = "CompetitionId")})
    @Column(name = "Slug")
    private Set<String> tags = new HashSet<>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Set<String> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CompetitionEntity that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
            id);
    }

    @Override
    public String toString() {
        return "CompetitionEntity{"
            + "id=" + id
            + "} " + super.toString();
    }
}
