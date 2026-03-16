package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;

public record CompetitionDTO(
    String id,
    String title,
    Double coordinateX,
    Double coordinateY,
    Long totalSubmissions,
    Long clusterId
) {

    public static CompetitionDTO ofCompetition(HasCompetitionData<?, ?, ?> competition) {
        return new CompetitionDTO(
            competition.getId().toString(),
            competition.getTitle(),
            competition.getCoordinateX(),
            competition.getCoordinateY(),
            competition.getTotalSubmissions(),
            competition.getCompetitionClusterId()
        );
    }
}
