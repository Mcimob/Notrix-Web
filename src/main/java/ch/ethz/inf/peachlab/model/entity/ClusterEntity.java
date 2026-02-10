package ch.ethz.inf.peachlab.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Entity
@NamedEntityGraph(name = ClusterEntity.WITH_KERNELS_AND_CELLS,
attributeNodes = {
    @NamedAttributeNode(value = "kernels", subgraph = KernelEntity.WITH_CELLS)
},
subgraphs = {
    @NamedSubgraph(name = KernelEntity.WITH_CELLS, attributeNodes = {
        @NamedAttributeNode("cells")
    })
})
public class ClusterEntity implements AbstractEntity, HasKernelData {

    public static final String WITH_KERNELS_AND_CELLS = "withKernelsAndCells";

    @Id
    @Column(name = "ClusterId")
    private Long id;

    @Column(nullable = true, name = "Summary", columnDefinition = "text")
    private String summary;

    @Column(nullable = true, name = "LocalClusterId")
    private Long localClusterId;

    @Column(nullable = false, name = "ClusterSize")
    private Long clusterSize;

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

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "ClusterId")
    private Set<KernelEntity> kernels;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SourceCompetitionId")
    private CompetitionEntity competition;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Map<Integer, Integer> getMainLabelStats() {
        return mainLabelStats;
    }

    public void setMainLabelStats(Map<Integer, Integer> mainLabelStats) {
        this.mainLabelStats = mainLabelStats;
    }

    public Set<KernelEntity> getKernels() {
        return kernels;
    }

    public void setKernels(Set<KernelEntity> kernels) {
        this.kernels = kernels;
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
    public Collection<HasKernelData> getChildren() {
        return kernels.stream().map(o -> (HasKernelData) o).toList();
    }


}
