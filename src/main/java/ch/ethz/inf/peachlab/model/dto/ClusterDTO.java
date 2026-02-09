package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.ClusterEntity;

public record ClusterDTO(
    Long clusterId,
    Long localClusterId,
    KernelDTO[] kernels
    ) {

    public static ClusterDTO ofCluster(ClusterEntity cluster) {
        return new ClusterDTO(
            cluster.getId(),
            cluster.getLocalClusterId(),
            cluster.getKernels().stream()
                .map(KernelDTO::ofKernel)
                .toArray(KernelDTO[]::new)
        );
    }
}
