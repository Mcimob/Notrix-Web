package ch.ethz.inf.peachlab.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
public class ClusterEntity implements AbstractEntity {

    @Id
    @Column(name = "ClusterId")
    private Long id;

    @Column(nullable = true, name = "Summary", columnDefinition = "text")
    private String summary;

    @Column(nullable = true, name = "LocalClusterId")
    private Long localClusterId;

    @Column(nullable = false, name = "ClusterSize")
    private Long clusterSize;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "TransitionMatrix", columnDefinition = "jsonb")
    private Integer[][] transitionMatrix;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "MainLabelStats", columnDefinition = "jsonb")
    private Map<Integer, Integer> mainLabelStats;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "ClusterId")
    private List<KernelEntity> kernels;

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

    public List<KernelEntity> getKernels() {
        return kernels;
    }

    public void setKernels(List<KernelEntity> kernels) {
        this.kernels = kernels;
    }
}
