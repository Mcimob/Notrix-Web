package ch.ethz.inf.peachlab.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.Objects;
import java.util.Set;

@Entity
public class CompetitionClusterEntity implements AbstractEntity<Long> {

    @Id
    @Column(name = "ClusterId")
    private Long id;

    @Column(name = "Description", columnDefinition = "VARCHAR")
    private String description;

    @Column(name = "CentroidX")
    private Double centroidX;

    @Column(name = "CentroidY")
    private Double centroidY;

    @Column(name = "RadiusX")
    private Double radiusX;

    @Column(name = "RadiusY")
    private Double radiusY;

    @Column(name = "StdX")
    private Double stdX;

    @Column(name = "StdY")
    private Double stdY;

    @OneToMany(mappedBy = "competitionClusterId")
    private Set<CompetitionEntity> competitions;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Double getCentroidX() {
        return centroidX;
    }

    public Double getCentroidY() {
        return centroidY;
    }

    public String getDescription() {
        return description;
    }

    public Double getRadiusX() {
        return radiusX;
    }

    public Double getRadiusY() {
        return radiusY;
    }

    public Double getStdX() {
        return stdX;
    }

    public Double getStdY() {
        return stdY;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CompetitionClusterEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id)
            && Objects.equals(description, that.description)
            && Objects.equals(centroidX, that.centroidX)
            && Objects.equals(centroidY, that.centroidY)
            && Objects.equals(radiusX, that.radiusX)
            && Objects.equals(radiusY, that.radiusY)
            && Objects.equals(stdX, that.stdX)
            && Objects.equals(stdY, that.stdY)
            && Objects.equals(competitions, that.competitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
            description,
            centroidX,
            centroidY,
            radiusX,
            radiusY,
            stdX,
            stdY,
            competitions);
    }

    @Override
    public String toString() {
        return "CompetitionClusterEntity{"
            + "centroidX=" + centroidX
            + ", id=" + id
            + ", description='" + description + '\''
            + ", centroidY=" + centroidY
            + ", radiusX=" + radiusX
            + ", radiusY=" + radiusY
            + ", stdX=" + stdX
            + ", stdY=" + stdY
            + ", competitions=" + competitions
            + '}';
    }
}
