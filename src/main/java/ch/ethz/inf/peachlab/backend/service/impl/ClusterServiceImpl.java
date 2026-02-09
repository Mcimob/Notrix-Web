package ch.ethz.inf.peachlab.backend.service.impl;

import ch.ethz.inf.peachlab.backend.dao.impl.ClusterDao;
import ch.ethz.inf.peachlab.backend.service.ClusterService;
import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.filter.ClusterFilter;
import org.springframework.stereotype.Service;

@Service
public class ClusterServiceImpl extends BaseServiceImpl<ClusterEntity, ClusterFilter> implements ClusterService {
    public ClusterServiceImpl(ClusterDao dao) {
        super(dao);
    }
}
