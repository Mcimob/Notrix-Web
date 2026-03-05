package ch.ethz.inf.peachlab.backend.service.db.impl;

import ch.ethz.inf.peachlab.backend.dao.db.impl.ClusterDao;
import ch.ethz.inf.peachlab.backend.service.db.ClusterService;
import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.filter.ClusterFilter;
import org.springframework.stereotype.Service;

@Service
public class ClusterServiceImpl extends BaseServiceImpl<ClusterEntity, ClusterFilter, Long> implements ClusterService {
    public ClusterServiceImpl(ClusterDao dao) {
        super(dao);
    }
}
