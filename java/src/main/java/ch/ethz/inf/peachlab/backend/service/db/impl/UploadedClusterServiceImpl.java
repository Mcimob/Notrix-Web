package ch.ethz.inf.peachlab.backend.service.db.impl;

import ch.ethz.inf.peachlab.backend.dao.db.impl.UploadedClusterDao;
import ch.ethz.inf.peachlab.backend.service.db.UploadedClusterService;
import ch.ethz.inf.peachlab.model.entity.UploadedClusterEntity;
import ch.ethz.inf.peachlab.model.filter.UploadedClusterFilter;
import org.springframework.stereotype.Service;

@Service
public class UploadedClusterServiceImpl extends BaseServiceImpl<UploadedClusterEntity, UploadedClusterFilter, Long> implements UploadedClusterService {

    public UploadedClusterServiceImpl(UploadedClusterDao dao) {
        super(dao);
    }
}
