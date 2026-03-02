package ch.ethz.inf.peachlab.backend.service.db.impl;

import ch.ethz.inf.peachlab.backend.dao.db.impl.UploadedCompetitionDao;
import ch.ethz.inf.peachlab.backend.service.db.UploadedCompetitionService;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.UploadedCompetitionFilter;
import org.springframework.stereotype.Service;

@Service
public class UploadedCompetitionServiceImpl extends BaseServiceImpl<UploadedCompetitionEntity, UploadedCompetitionFilter, String> implements UploadedCompetitionService {

    public UploadedCompetitionServiceImpl(UploadedCompetitionDao dao) {
        super(dao);
    }
}
