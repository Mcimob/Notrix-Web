package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.CompetitionClusterEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionClusterFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionClusterRepository extends BaseRepository<CompetitionClusterEntity, CompetitionClusterFilter, Long> {
}
