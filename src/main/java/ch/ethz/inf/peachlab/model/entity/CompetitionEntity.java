package ch.ethz.inf.peachlab.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
public class CompetitionEntity implements AbstractEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subtitle;

    @Column(nullable = true, columnDefinition = "varchar")
    private String overview;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private Long totalSubmissions;

    @Column(nullable = false)
    private LocalDateTime deadlineDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "MainLabelStats", columnDefinition = "jsonb")
    private Map<Integer, Integer> mainLabelStats;

    @Column(nullable = false)
    private Double avgCellsPerKernel;

    @Column(nullable = false)
    private Double avgVotes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "TransitionMatrix", columnDefinition = "jsonb")
    private Integer[][] transitionMatrix;

    @ElementCollection
    @CollectionTable(name = "CompetitionTags", joinColumns = {@JoinColumn(name = "CompetitionId")})
    @Column(name = "Slug")
    private Set<String> tags = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "SourceCompetitionId")
    private Set<KernelEntity> kernels;

    @OneToMany
    @JoinColumn(name = "CompetitionId")
    private Set<ClusterEntity> clusters;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String description) {
        this.subtitle = description;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overiew) {
        this.overview = overiew;
    }

    public Long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(Long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Map<Integer, Integer> getMainLabelStats() {
        return mainLabelStats;
    }

    public void setMainLabelStats(Map<Integer, Integer> mainLabelStats) {
        this.mainLabelStats = mainLabelStats;
    }

    public Double getAvgCellsPerKernel() {
        return avgCellsPerKernel;
    }

    public void setAvgCellsPerKernel(Double avgCellsPerKernel) {
        this.avgCellsPerKernel = avgCellsPerKernel;
    }

    public Double getAvgVotes() {
        return avgVotes;
    }

    public void setAvgVotes(Double avgVotes) {
        this.avgVotes = avgVotes;
    }

    public Integer[][] getTransitionMatrix() {
        return transitionMatrix;
    }

    public void setTransitionMatrix(Integer[][] transitionMatrix) {
        this.transitionMatrix = transitionMatrix;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<KernelEntity> getKernels() {
        return kernels;
    }

    public void setKernels(Set<KernelEntity> kernels) {
        this.kernels = kernels;
    }

    public Set<ClusterEntity> getClusters() {
        return clusters;
    }

    public void setClusters(Set<ClusterEntity> clusters) {
        this.clusters = clusters;
    }

    @Override
    public String toString() {
        return "CompetitionEntity{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", description='" + subtitle + '\''
                + ", slug='" + slug + '\''
                + ", totalSubmissions=" + totalSubmissions
                + ", deadlineDate=" + deadlineDate
                + "} " + super.toString();
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
        return Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(subtitle, that.subtitle)
                && Objects.equals(slug, that.slug)
                && Objects.equals(totalSubmissions, that.totalSubmissions)
                && Objects.equals(deadlineDate, that.deadlineDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                id,
                title,
                subtitle,
                slug,
                totalSubmissions,
                deadlineDate);
    }
}
