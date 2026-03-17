package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.CompetitionClusterEntity;

public record CompetitionClusterDTO(
    Long id,
    String description,
    Double centroidX,
    Double centroidY,
    Double radiusX,
    Double radiusY,
    Double stdX,
    Double stdY
) {
    public static CompetitionClusterDTO ofCompetitionCluster(CompetitionClusterEntity cluster) {
        return new CompetitionClusterDTO(
            cluster.getId(),
            cluster.getDescription(),
            cluster.getCentroidX(),
            cluster.getCentroidY(),
            cluster.getRadiusX(),
            cluster.getRadiusY(),
            cluster.getStdX(),
            cluster.getStdY()
        );
    }
}
