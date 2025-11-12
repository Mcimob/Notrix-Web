package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionRepository extends BaseRepository<CompetitionEntity, CompetitionFilter> {
}
