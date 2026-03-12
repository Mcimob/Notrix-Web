package ch.ethz.inf.peachlab.model.entity;

import ch.ethz.inf.peachlab.model.enums.MainLabel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@MappedSuperclass
public abstract class HasKernelData<ID, C extends HasCellData, CO extends HasCompetitionData<?, ?, ?>> implements AbstractEntity<ID>, HasBaseStats {

    @Serial
    private static final long serialVersionUID = -8687363840493102608L;

    @Column(nullable = false, name = "CreationDate")
    protected LocalDateTime creationDate;

    @Column(nullable = true, name = "VersionNumber")
    protected Integer versionNumber;

    @Column(nullable = true, name = "Title")
    protected String title;

    @Column(nullable = false, name = "TotalVotes")
    protected Integer totalVotes = 0;

    @Column(nullable = false, name = "TotalViews")
    protected int totalViews = 0;

    @Column(nullable = false, name = "TotalComments")
    protected int totalComments = 0;

    @Column(nullable = true, name = "CurrentUrlSlug")
    protected String currentUrlSlug;

    @Column(nullable = true, name = "AuthorUserName")
    protected String authorUserName;

    @Column(nullable = true, name = "AuthorDisplayName")
    protected String authorDisplayName;

    @Column(nullable = false, name = "NumLines")
    protected Integer numLines;

    @Column(nullable = false, name="CellCount")
    protected Integer cellCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "LabelSequence", columnDefinition = "jsonb")
    protected MainLabel[] labelSequence;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "TransitionMatrix", columnDefinition = "jsonb")
    protected Integer[][] transitionMatrix;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "TransitionMatrixNorm", columnDefinition = "jsonb")
    protected Integer[][] transitionMatrixNorm;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "MainLabelStats", columnDefinition = "jsonb")
    protected Map<Integer, Integer> mainLabelStats;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "MainLabelStatsNorm", columnDefinition = "jsonb")
    protected List<Double> mainLabelStatsNorm;
    
    @Column(name = "NGrams",columnDefinition = "varchar")
    protected String nGrams;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ComplexitiFeaturesNorm", columnDefinition = "jsonb")
    protected List<Double> complexityFeaturesNorm;

    @Column(name = "SourceCompetitionId")
    protected ID sourceCompetitionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SourceCompetitionId", insertable = false, updatable = false)
    protected CO competition;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "KernelVersionId")
    @OrderColumn(name = "CellId")
    private List<C> cells;

    @Column(name = "ClusterId")
    protected Long clusterId;

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public String getCurrentUrlSlug() {
        return currentUrlSlug;
    }

    public void setCurrentUrlSlug(String currentUrlSlug) {
        this.currentUrlSlug = currentUrlSlug;
    }

    public String getAuthorUserName() {
        return authorUserName;
    }

    public void setAuthorUserName(String authorUserName) {
        this.authorUserName = authorUserName;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public Integer getNumLines() {
        return numLines;
    }

    public void setNumLines(Integer numLines) {
        this.numLines = numLines;
    }

    public Integer getCellCount() {
        return cellCount;
    }

    public void setCellCount(Integer cellCount) {
        this.cellCount = cellCount;
    }

    public MainLabel[] getLabelSequence() {
        return labelSequence;
    }

    public void setLabelSequence(MainLabel[] labelSequence) {
        this.labelSequence = labelSequence;
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

    public ID getSourceCompetitionId() {
        return sourceCompetitionId;
    }

    public void setSourceCompetitionId(ID sourceCompetitionId) {
        this.sourceCompetitionId = sourceCompetitionId;
    }

    public CO getCompetition() {
        return competition;
    }

    public void setCompetition(CO competition) {
        this.competition = competition;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public List<C> getCells() {
        return cells;
    }

    public void setCells(List<C> cells) {
        this.cells = cells;
    }

    @Override
    public Double getVotes() {
        return totalVotes.doubleValue();
    }

    @Override
    public Collection<HasBaseStats> getChildren() {
        return List.of();
    }

    @Override
    public Double getLines() {
        return numLines.doubleValue();
    }

    @Override
    public Double getNumCells() {
        return cellCount.doubleValue();
    }

    public String getUrlParameter() {
        if (authorDisplayName == null || currentUrlSlug == null)
            return getId().toString();
        return "%s/%s".formatted(authorUserName, currentUrlSlug);
    }

    public HasCompetitionData<?, ?, ?> getRelevantCompetition() {
        return competition;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HasKernelData<?, ?, ?> that)) {
            return false;
        }
        return totalViews == that.totalViews
            && totalComments == that.totalComments
            && Objects.equals(creationDate, that.creationDate)
            && Objects.equals(title, that.title)
            && Objects.equals(totalVotes, that.totalVotes)
            && Objects.equals(currentUrlSlug, that.currentUrlSlug)
            && Objects.equals(authorUserName, that.authorUserName)
            && Objects.equals(authorDisplayName, that.authorDisplayName)
            && Objects.equals(numLines, that.numLines)
            && Objects.equals(cellCount, that.cellCount)
            && Objects.equals(sourceCompetitionId, that.sourceCompetitionId)
            && Objects.equals(clusterId, that.clusterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creationDate,
            title,
            totalVotes,
            totalViews,
            totalComments,
            currentUrlSlug,
            authorUserName,
            authorDisplayName,
            numLines,
            cellCount,
            sourceCompetitionId,
            clusterId);
    }

    @Override
    public String toString() {
        return "HasKernelData{"
            + "creationDate=" + creationDate
            + ", title='" + title + '\''
            + ", totalVotes=" + totalVotes
            + ", totalViews=" + totalViews
            + ", totalComments=" + totalComments
            + ", currentUrlSlug='" + currentUrlSlug + '\''
            + ", authorUserName='" + authorUserName + '\''
            + ", authorDisplayName='" + authorDisplayName + '\''
            + ", numLines=" + numLines
            + ", cellCount=" + cellCount
            + ", sourceCompetitionId=" + sourceCompetitionId
            + ", clusterId=" + clusterId
            + '}';
    }
}
