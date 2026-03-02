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

import java.io.Serial;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
public class ClusterEntity extends HasClusterData<KernelEntity, CompetitionEntity> {

    public static final String WITH_KERNELS_AND_CELLS = "withKernelsAndCells";
    @Serial
    private static final long serialVersionUID = 7054276197678850847L;

    @Id
    @Column(name = "ClusterId")
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClusterEntity that)) {
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
        return "ClusterEntity{"
            + "id=" + id
            + "} " + super.toString();
    }
}
