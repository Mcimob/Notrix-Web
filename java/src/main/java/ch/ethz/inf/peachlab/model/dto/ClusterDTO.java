package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.HasClusterData;

import java.io.Serializable;

public record ClusterDTO(
    Long clusterId,
    Long localClusterId,
    KernelDTO[] kernels
    ) implements Serializable {

    public static ClusterDTO ofCluster(HasClusterData<?, ?> cluster) {
        return new ClusterDTO(
            cluster.getId(),
            cluster.getLocalClusterId(),
            cluster.getKernels().stream()
                .map(KernelDTO::ofKernel)
                .toArray(KernelDTO[]::new)
        );
    }
}
