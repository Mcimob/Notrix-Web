package ch.ethz.inf.peachlab.model.entity;

import ch.ethz.inf.peachlab.model.enums.CellType;
import ch.ethz.inf.peachlab.model.enums.MainLabel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

import java.io.Serial;
import java.util.Objects;

@MappedSuperclass
public abstract class HasCellData implements AbstractEntity<Long> {
    @Serial
    private static final long serialVersionUID = 2815936800622593631L;

    @Column(nullable = false, name = "CellId")
    @JsonProperty("CellId")
    private int cellId;

    @Column(nullable = true, name = "Source", columnDefinition = "text")
    @JsonProperty("Source")
    private String source;

    @Column(nullable = false, name = "SourceLineCount")
    @JsonProperty("SourceLineCount")
    private int sourceLinesCount;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, name = "CellType")
    @JsonProperty("CellType")
    private CellType cellType;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = true, name = "MainLabel")
    @JsonProperty("MainLabel")
    private MainLabel mainLabel;

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public MainLabel getMainLabel() {
        return mainLabel;
    }

    public void setMainLabel(MainLabel mainLabel) {
        this.mainLabel = mainLabel;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getSourceLinesCount() {
        return sourceLinesCount;
    }

    public void setSourceLinesCount(int sourceLinesCount) {
        this.sourceLinesCount = sourceLinesCount;
    }

    @Override
    public String toString() {
        return "HasCellData{"
            + "cellId=" + cellId
            + ", source='" + source + '\''
            + ", sourceLinesCount=" + sourceLinesCount
            + ", cellType=" + cellType
            + ", mainLabel=" + mainLabel
            + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HasCellData that)) {
            return false;
        }
        return cellId == that.cellId
            && sourceLinesCount == that.sourceLinesCount
            && Objects.equals(source, that.source)
            && cellType == that.cellType
            && mainLabel == that.mainLabel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellId,
            source,
            sourceLinesCount,
            cellType,
            mainLabel);
    }
}
