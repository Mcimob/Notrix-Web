package ch.ethz.inf.peachlab.model.entity;

import ch.ethz.inf.peachlab.model.enums.MainLabel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@MappedSuperclass
public abstract class HasClusterData<K extends HasKernelData<?, ?>, C extends HasCompetitionData<?, K, ?>> implements AbstractEntity<Long>, HasBaseStats {
    @Serial
    private static final long serialVersionUID = -2133425797254907724L;

    @Column(nullable = true, name = "Summary", columnDefinition = "text")
    private String summary;

    @Column(nullable = true, name = "LocalClusterId")
    private Long localClusterId;

    @Column(nullable = false, name = "ClusterSize")
    private Long clusterSize;

    @Column(nullable = false)
    private Double avgCellsPerKernel;

    @Column(nullable = false)
    private Double avgVotes = 0D;

    @Column(nullable = false)
    private Double avgLinesPerKernel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "TransitionMatrix", columnDefinition = "jsonb")
    private Integer[][] transitionMatrix;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "MainLabelStats", columnDefinition = "jsonb")
    private Map<Integer, Integer> mainLabelStats;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ClusterId")
    private Set<K> kernels;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SourceCompetitionId")
    private C competition;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getLocalClusterId() {
        return localClusterId;
    }

    public void setLocalClusterId(Long localClusterId) {
        this.localClusterId = localClusterId;
    }

    public Long getClusterSize() {
        return clusterSize;
    }

    public void setClusterSize(Long clusterSize) {
        this.clusterSize = clusterSize;
    }

    public Integer[][] getTransitionMatrix() {
        return transitionMatrix;
    }

    public void setTransitionMatrix(Integer[][] transitionMatrix) {
        this.transitionMatrix = transitionMatrix;
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

    public void setMainLabelStats(Map<Integer, Integer> mainLabelStats) {
        this.mainLabelStats = mainLabelStats;
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

    @Override
    public Double getLines() {
        return avgLinesPerKernel;
    }

    @Override
    public Double getNumCells() {
        return avgCellsPerKernel;
    }

    @Override
    public Double getVotes() {
        return avgVotes;
    }

    @Override
    public Collection<HasBaseStats> getChildren() {
        return getKernels().stream()
            .map(o -> (HasBaseStats) o)
            .toList();
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

    public C getCompetition() {
        return competition;
    }

    public void setCompetition(C competition) {
        this.competition = competition;
    }

    public void setKernels(Set<K> kernels) {
        this.kernels = kernels;
    }

    public Set<K> getKernels() {
        return kernels;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HasClusterData<?, ?> that)) {
            return false;
        }
        return Objects.equals(summary, that.summary)
            && Objects.equals(localClusterId, that.localClusterId)
            && Objects.equals(clusterSize, that.clusterSize)
            && Objects.equals(avgCellsPerKernel, that.avgCellsPerKernel)
            && Objects.equals(avgVotes, that.avgVotes)
            && Objects.equals(avgLinesPerKernel, that.avgLinesPerKernel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summary,
            localClusterId,
            clusterSize,
            avgCellsPerKernel,
            avgVotes,
            avgLinesPerKernel);
    }

    @Override
    public String toString() {
        return "HasClusterData{"
            + "avgCellsPerKernel=" + avgCellsPerKernel
            + ", summary='" + summary + '\''
            + ", localClusterId=" + localClusterId
            + ", clusterSize=" + clusterSize
            + ", avgVotes=" + avgVotes
            + ", avgLinesPerKernel=" + avgLinesPerKernel
            + '}';
    }
}
