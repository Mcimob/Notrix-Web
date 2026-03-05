package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.UploadedClusterEntity;
import ch.ethz.inf.peachlab.model.filter.UploadedClusterFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadedClusterRepository extends BaseRepository<UploadedClusterEntity, UploadedClusterFilter, Long> {
}
