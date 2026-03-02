package ch.ethz.inf.peachlab.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;

import java.io.Serial;
import java.util.List;
import java.util.Objects;

@Entity
@NamedEntityGraph(name = KernelEntity.WITH_CELLS,
    attributeNodes = {
        @NamedAttributeNode("cells")
    })
@NamedEntityGraph(name = KernelEntity.WITH_COMPETITION,
    attributeNodes = {
        @NamedAttributeNode("competition")
    })
public class KernelEntity extends HasKernelData<Long, CellEntity> {

    public static final String WITH_CELLS = "withCells";
    public static final String WITH_COMPETITION = "withCompetition";

    @Serial
    private static final long serialVersionUID = -4963973968059218651L;

    @Id
    @Column(nullable = false, name = "KernelVersionId")
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "KernelVersionId")
    @OrderColumn(name = "CellId")
    private List<CellEntity> cells;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public List<CellEntity> getCells() {
        return cells;
    }

    @Override
    public void setCells(List<CellEntity> cells) {
        this.cells = cells;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KernelEntity that)) {
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
        return "KernelEntity{"
            + "id=" + id
            + "} " + super.toString();
    }
}
