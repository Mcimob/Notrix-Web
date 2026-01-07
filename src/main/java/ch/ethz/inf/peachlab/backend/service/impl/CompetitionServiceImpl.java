package ch.ethz.inf.peachlab.backend.service.impl;

import ch.ethz.inf.peachlab.backend.dao.impl.CompetitionDao;
import ch.ethz.inf.peachlab.backend.service.CompetitionService;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import org.springframework.stereotype.Service;

@Service
public class CompetitionServiceImpl extends BaseServiceImpl<CompetitionEntity, CompetitionFilter> implements CompetitionService {

    public CompetitionServiceImpl(CompetitionDao dao) {
        super(dao);
    }
}
