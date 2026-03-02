package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.UploadedCompetitionFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadedCompetitionRepository extends BaseRepository<UploadedCompetitionEntity, UploadedCompetitionFilter, String> {
}
