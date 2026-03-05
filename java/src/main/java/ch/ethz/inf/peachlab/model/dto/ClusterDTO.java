package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.HasClusterData;

public record ClusterDTO(
    Long clusterId,
    Long localClusterId,
    KernelDTO[] kernels
    ) {

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
