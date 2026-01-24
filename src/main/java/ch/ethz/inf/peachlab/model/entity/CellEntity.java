package ch.ethz.inf.peachlab.model.entity;

import ch.ethz.inf.peachlab.model.enums.CellType;
import ch.ethz.inf.peachlab.model.enums.MainLabel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class CellEntity implements AbstractEntity {

    @Id
    private Long id;

    @Column(nullable = false, name = "CellId")
    private int cellId;

    @Column(nullable = true, name = "Source", columnDefinition = "text")
    private String source;

    @Column(nullable = false, name = "SourceLineCount")
    private int sourceLinesCount;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, name = "CellType")
    private CellType cellType;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = true, name = "MainLabel")
    private MainLabel mainLabel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kernelVersionId")
    private KernelEntity kernel;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
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

    @Override
    public String toString() {
        return "CellEntity{"
                + "id=" + id
                + ", cellId=" + cellId
                + ", source='" + source + '\''
                + ", cellType=" + cellType
                + ", mainLabel=" + mainLabel
                + "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CellEntity that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return cellId == that.cellId
                && Objects.equals(id, that.id)
                && Objects.equals(source, that.source)
                && cellType == that.cellType
                && Objects.equals(mainLabel, that.mainLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                id,
                cellId,
                source,
                cellType,
                mainLabel);
    }
}
