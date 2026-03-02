package ch.ethz.inf.peachlab.model.entity;

import ch.ethz.inf.peachlab.model.enums.MainLabel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@NamedEntityGraph(name = UploadedKernelEntity.WITH_CELLS_UPLOADED,
    attributeNodes = {
        @NamedAttributeNode("cells")
    })
@NamedEntityGraph(name = UploadedKernelEntity.WITH_COMPETITION_UPLOADED,
    attributeNodes = {
        @NamedAttributeNode("competition")
    })
public class UploadedKernelEntity extends HasKernelData<String, UploadedCellEntity> {

    public static final String WITH_CELLS_UPLOADED = "withCellsUploaded";
    public static final String WITH_COMPETITION_UPLOADED = "withCompetitionUploaded";
    
    @Serial
    private static final long serialVersionUID = -7032933906001116015L;

    @Id
    @Column(nullable = false, name = "KernelVersionId")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "KernelVersionId")
    @OrderColumn(name = "CellId")
    private List<UploadedCellEntity> cells;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SourceCompetitionId", insertable = false, updatable = false)
    protected UploadedCompetitionEntity uploadedCompetition;

    @Override
    public String getId() {
        return id;
    }

    @Override
    @JsonProperty("CreationDate")
    public void setCreationDate(LocalDateTime creationDate) {
        super.setCreationDate(creationDate);
    }

    @Override
    @JsonProperty("Title")
    public void setTitle(String title) {
        super.setTitle(title);
    }

    @Override
    @JsonProperty("TotalVotes")
    public void setTotalVotes(Integer totalVotes) {
        super.setTotalVotes(totalVotes);
    }

    @Override
    @JsonProperty("TotalViews")
    public void setTotalViews(int totalViews) {
        super.setTotalViews(totalViews);
    }

    @Override
    @JsonProperty("TotalComments")
    public void setTotalComments(int totalComments) {
        super.setTotalComments(totalComments);
    }

    @Override
    @JsonProperty("CurrentUrlSlug")
    public void setCurrentUrlSlug(String currentUrlSlug) {
        super.setCurrentUrlSlug(currentUrlSlug);
    }

    @Override
    @JsonProperty("AuthorUserName")
    public void setAuthorUserName(String authorUserName) {
        super.setAuthorUserName(authorUserName);
    }

    @Override
    @JsonProperty("AuthorDisplayName")
    public void setAuthorDisplayName(String authorDisplayName) {
        super.setAuthorDisplayName(authorDisplayName);
    }

    @Override
    @JsonProperty("NumLines")
    public void setNumLines(Integer numLines) {
        super.setNumLines(numLines);
    }

    @Override
    @JsonProperty("CellCount")
    public void setCellCount(Integer cellCount) {
        super.setCellCount(cellCount);
    }

    @Override
    @JsonProperty("LabelSequence")
    public void setLabelSequence(MainLabel[] labelSequence) {
        super.setLabelSequence(labelSequence);
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
    @JsonProperty("SourceCompetitionId")
    public void setSourceCompetitionId(Long sourceCompetitionId) {
        super.setSourceCompetitionId(sourceCompetitionId);
    }

    @Override
    @JsonProperty("KernelVersionId")
    public void setId(String id) {
        this.id = id;
    }

    @Override
    @JsonProperty("ClusterId")
    public void setClusterId(Long clusterId) {
        super.setClusterId(clusterId);
    }

    @Override
    public List<UploadedCellEntity> getCells() {
        return cells;
    }

    @Override
    @JsonProperty("cells")
    public void setCells(List<UploadedCellEntity> cells) {
        this.cells = cells;
    }

    public UploadedCompetitionEntity getUploadedCompetition() {
        return uploadedCompetition;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UploadedKernelEntity that)) {
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
        return "UploadedKernelEntity{"
            + "id='" + id + '\''
            + "} " + super.toString();
    }
}
