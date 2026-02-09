package ch.ethz.inf.peachlab.model.entity;

import ch.ethz.inf.peachlab.model.enums.MainLabel;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Column(nullable = false)
    private Double avgCellsPerKernel;

    @Column(nullable = false)
    private Double avgVotes;

    @Column(nullable = false)
    private Double avgLinesPerKernel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "TransitionMatrix", columnDefinition = "jsonb")
    private Integer[][] transitionMatrix;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "MainLabelStats", columnDefinition = "jsonb")
    private Map<Integer, Integer> mainLabelStats;

    @ElementCollection
    @CollectionTable(name = "CompetitionTags", joinColumns = {@JoinColumn(name = "CompetitionId")})
    @Column(name = "Slug")
    private Set<String> tags = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "SourceCompetitionId")
    private Set<KernelEntity> kernels;

    @OneToMany
    @JoinColumn(name = "SourceCompetitionId")
    @OrderColumn(name = "LocalClusterId")
    private List<ClusterEntity> clusters;

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

    public String getSubtitle() {
        return subtitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getSlug() {
        return slug;
    }

    public Long getTotalSubmissions() {
        return totalSubmissions;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public Double getAvgCellsPerKernel() {
        return avgCellsPerKernel;
    }

    public Double getAvgVotes() {
        return avgVotes;
    }

    public Double getAvgLinesPerKernel() {
        return avgLinesPerKernel;
    }

    public Integer[][] getTransitionMatrix() {
        return transitionMatrix;
    }

    public Map<MainLabel, Integer> getMainLabelStats() {
        if (mainLabelStats == null) {
            return Map.of();
        }

        return mainLabelStats.entrySet().stream()
            .collect(Collectors.toMap(
                e -> MainLabel.values()[e.getKey()],
                Map.Entry::getValue));
    }

    public Set<String> getTags() {
        return tags;
    }

    public Set<KernelEntity> getKernels() {
        return kernels;
    }

    public List<ClusterEntity> getClusters() {
        return clusters;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CompetitionEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(subtitle, that.subtitle)
                && Objects.equals(overview, that.overview)
                && Objects.equals(slug, that.slug)
                && Objects.equals(totalSubmissions, that.totalSubmissions)
                && Objects.equals(deadlineDate, that.deadlineDate)
                && Objects.equals(avgCellsPerKernel, that.avgCellsPerKernel)
                && Objects.equals(avgVotes, that.avgVotes)
                && Objects.equals(avgLinesPerKernel, that.avgLinesPerKernel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                title,
                subtitle,
                overview,
                slug,
                totalSubmissions,
                deadlineDate,
                avgCellsPerKernel,
                avgVotes,
                avgLinesPerKernel);
    }

    @Override
    public String toString() {
        return "CompetitionEntity{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", subtitle='" + subtitle + '\''
                + ", overview='" + overview + '\''
                + ", slug='" + slug + '\''
                + ", totalSubmissions=" + totalSubmissions
                + ", deadlineDate=" + deadlineDate
                + ", avgCellsPerKernel=" + avgCellsPerKernel
                + ", avgVotes=" + avgVotes
                + ", avgLinesPerKernel=" + avgLinesPerKernel
                + '}';
    }
}
