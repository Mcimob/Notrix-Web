package ch.ethz.inf.peachlab.model.entity;

import ch.ethz.inf.peachlab.model.enums.MainLabel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@MappedSuperclass
public abstract class HasCompetitionData<ID, K extends HasKernelData<?, ?>, C extends HasClusterData<?, ?>> implements AbstractEntity<ID> {
    @Serial
    private static final long serialVersionUID = -7446297828210625066L;

    @Column(nullable = true)
    protected String title;

    @Column(nullable = true)
    protected String subtitle;

    @Column(nullable = true, columnDefinition = "varchar")
    protected String overview;

    @Column(nullable = true)
    protected String slug;

    @Column(nullable = false)
    protected Long totalSubmissions;

    @Column(nullable = true)
    protected LocalDateTime deadlineDate;

    @Column(nullable = false)
    protected Double avgCellsPerKernel;

    @Column(nullable = false)
    protected Double avgVotes = 0D;

    @Column(nullable = false)
    protected Double avgLinesPerKernel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "TransitionMatrix", columnDefinition = "jsonb")
    protected Integer[][] transitionMatrix;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "MainLabelStats", columnDefinition = "jsonb")
    protected Map<Integer, Integer> mainLabelStats;

    @OneToMany
    @JoinColumn(name = "SourceCompetitionId")
    protected Set<K> kernels;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "SourceCompetitionId")
    @OrderColumn(name = "LocalClusterId")
    protected List<C> clusters;

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

    public Set<K> getKernels() {
        return kernels;
    }

    public List<C> getClusters() {
        return clusters;
    }

    public void setAvgCellsPerKernel(Double avgCellsPerKernel) {
        this.avgCellsPerKernel = avgCellsPerKernel;
    }

    public void setAvgLinesPerKernel(Double avgLinesPerKernel) {
        this.avgLinesPerKernel = avgLinesPerKernel;
    }

    public void setAvgVotes(Double avgVotes) {
        this.avgVotes = avgVotes;
    }

    public void setClusters(List<C> clusters) {
        this.clusters = clusters;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public void setKernels(Set<K> kernels) {
        this.kernels = kernels;
    }

    public void setMainLabelStats(Map<Integer, Integer> mainLabelStats) {
        this.mainLabelStats = mainLabelStats;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTotalSubmissions(Long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public void setTransitionMatrix(Integer[][] transitionMatrix) {
        this.transitionMatrix = transitionMatrix;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HasCompetitionData<?, ?, ?> that)) {
            return false;
        }
        return Objects.equals(title, that.title)
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
        return Objects.hash(title,
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
            + "title='" + title + '\''
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
