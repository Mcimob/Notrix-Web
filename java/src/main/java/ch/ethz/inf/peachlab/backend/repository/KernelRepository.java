package ch.ethz.inf.peachlab.backend.repository;

import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface KernelRepository extends BaseRepository<KernelEntity, KernelFilter, Long> {
}
