package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.filter.ClusterFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterRepository extends BaseRepository<ClusterEntity, ClusterFilter, Long> {
}
