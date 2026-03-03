package ch.ethz.inf.peachlab.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;

import java.io.Serial;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
@NamedEntityGraph(name = UploadedClusterEntity.WITH_KERNELS_AND_CELLS_UPLOADED,
    attributeNodes = {
        @NamedAttributeNode(value = "kernels", subgraph = UploadedKernelEntity.WITH_CELLS_UPLOADED)
    },
    subgraphs = {
        @NamedSubgraph(name = UploadedKernelEntity.WITH_CELLS_UPLOADED, attributeNodes = {
            @NamedAttributeNode("cells")
        })
    })
public class UploadedClusterEntity extends HasClusterData<UploadedKernelEntity, UploadedCompetitionEntity> {

    public static final String WITH_KERNELS_AND_CELLS_UPLOADED = "withKernelsAndCellsUploaded";
    @Serial
    private static final long serialVersionUID = -7907921108806369603L;

    @Id
    @Column(name = "ClusterId")
    @GeneratedValue
    private long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    @JsonProperty("Summary")
    public void setSummary(String summary) {
        super.setSummary(summary);
    }

    @Override
    @JsonProperty("LocalClusterId")
    public void setLocalClusterId(Long localClusterId) {
        super.setLocalClusterId(localClusterId);
    }

    @Override
    @JsonProperty("ClusterSize")
    public void setClusterSize(Long clusterSize) {
        super.setClusterSize(clusterSize);
    }

    @Override
    @JsonProperty("TransitionMatrix")
    public void setTransitionMatrix(Integer[][] transitionMatrix) {
        super.setTransitionMatrix(transitionMatrix);
    }

    @Override
    @JsonProperty("MainLabelStats")
    public void setMainLabelStats(Map<Integer, Integer> mainLabelStats) {
        super.setMainLabelStats(mainLabelStats);
    }

    @Override
    @JsonProperty("kernels")
    public void setKernels(Set<UploadedKernelEntity> kernels) {
        super.setKernels(kernels);
    }

    @Override
    @JsonProperty("AvgCellsPerKernel")
    public void setAvgCellsPerKernel(Double avgCellsPerKernel) {
        super.setAvgCellsPerKernel(avgCellsPerKernel);
    }

    @Override
    @JsonProperty("AvgLinesPerKernel")
    public void setAvgLinesPerKernel(Double avgLinesPerKernel) {
        super.setAvgLinesPerKernel(avgLinesPerKernel);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UploadedClusterEntity that)) {
            return false;
        }
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UploadedClusterEntity{"
            + "id=" + id
            + "} " + super.toString();
    }
}
