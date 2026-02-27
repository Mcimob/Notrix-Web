package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.filter.UploadedKernelFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadedKernelRepository extends BaseRepository<UploadedKernelEntity, UploadedKernelFilter, String> {
}
