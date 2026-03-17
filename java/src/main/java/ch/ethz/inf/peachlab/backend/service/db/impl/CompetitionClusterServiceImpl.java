package ch.ethz.inf.peachlab.backend.service.db.impl;

import ch.ethz.inf.peachlab.backend.dao.db.impl.CompetitionClusterDao;
import ch.ethz.inf.peachlab.backend.service.db.CompetitionClusterService;
import ch.ethz.inf.peachlab.model.entity.CompetitionClusterEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionClusterFilter;
import org.springframework.stereotype.Service;

@Service
public class CompetitionClusterServiceImpl extends BaseServiceImpl<CompetitionClusterEntity, CompetitionClusterFilter, Long> implements CompetitionClusterService {
    public CompetitionClusterServiceImpl(CompetitionClusterDao dao) {
        super(dao);
    }
}
