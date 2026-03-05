package ch.ethz.inf.peachlab.backend.dao.db.impl;

import ch.ethz.inf.peachlab.backend.repository.UploadedClusterRepository;
import ch.ethz.inf.peachlab.model.entity.UploadedClusterEntity;
import ch.ethz.inf.peachlab.model.filter.UploadedClusterFilter;
import org.springframework.stereotype.Component;

@Component
public class UploadedClusterDao extends BaseDaoImpl<UploadedClusterEntity, UploadedClusterFilter, Long> {

    public UploadedClusterDao(UploadedClusterRepository repository) {
        super(repository);
    }
}
