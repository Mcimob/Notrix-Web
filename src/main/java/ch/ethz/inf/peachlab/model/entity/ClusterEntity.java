package ch.ethz.inf.peachlab.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class ClusterEntity implements AbstractEntity {

    @Id
    private Long id;

    @Column(nullable = true, name = "Description")
    private String description;

    @OneToMany
    @JoinColumn(name = "cluster_id")
    private List<KernelEntity> kernels;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
