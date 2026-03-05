package ch.ethz.inf.peachlab.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serial;
import java.util.Objects;

@Entity
public class UploadedCellEntity extends HasCellData {
    @Serial
    private static final long serialVersionUID = 1846332054338692995L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KernelVersionId")
    private UploadedKernelEntity uploadedKernel;

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
        if (!(o instanceof UploadedCellEntity that)) {
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
        return "UploadedCellEntity{"
            + "id=" + id
            + "} " + super.toString();
    }
}
