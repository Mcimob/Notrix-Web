package ch.ethz.inf.peachlab.backend.service.db.impl;

import ch.ethz.inf.peachlab.backend.dao.db.impl.CompetitionDao;
import ch.ethz.inf.peachlab.backend.service.db.CompetitionService;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import org.springframework.stereotype.Service;

@Service
public class CompetitionServiceImpl extends BaseServiceImpl<CompetitionEntity, CompetitionFilter, Long> implements CompetitionService {

    public CompetitionServiceImpl(CompetitionDao dao) {
        super(dao);
    }
}
