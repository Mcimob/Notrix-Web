package ch.ethz.inf.peachlab.backend.dao.db.impl;

import ch.ethz.inf.peachlab.backend.repository.UploadedCompetitionRepository;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.UploadedCompetitionFilter;
import org.springframework.stereotype.Component;

@Component
public class UploadedCompetitionDao extends BaseDaoImpl<UploadedCompetitionEntity, UploadedCompetitionFilter, String> {

    public UploadedCompetitionDao(UploadedCompetitionRepository repository) {
        super(repository);
    }
}
