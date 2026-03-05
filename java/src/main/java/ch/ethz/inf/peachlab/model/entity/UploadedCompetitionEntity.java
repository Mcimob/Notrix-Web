package ch.ethz.inf.peachlab.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
public class UploadedCompetitionEntity extends HasCompetitionData<String, UploadedKernelEntity, UploadedClusterEntity> {

    @Serial
    private static final long serialVersionUID = 1565314587104368754L;

    @Id
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    @JsonProperty("Id")
    public void setId(String id) {
        this.id = id;
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
    @JsonProperty("clusters")
    public void setClusters(List<UploadedClusterEntity> clusters) {
        super.setClusters(clusters);
    }

    @Override
    @JsonProperty("MainLabelStats")
    public void setMainLabelStats(Map<Integer, Integer> mainLabelStats) {
        super.setMainLabelStats(mainLabelStats);
    }

    @Override
    @JsonProperty("TransitionMatrix")
    public void setTransitionMatrix(Integer[][] transitionMatrix) {
        super.setTransitionMatrix(transitionMatrix);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UploadedCompetitionEntity that)) {
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
        return "UploadedCompetitionEntity{"
            + "id='" + id + '\''
            + "} " + super.toString();
    }
}
