package ch.ethz.inf.peachlab.backend.dao.impl;

import ch.ethz.inf.peachlab.backend.repository.CompetitionRepository;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import org.springframework.stereotype.Component;

@Component
public class CompetitionDao extends BaseDaoImpl<CompetitionEntity, CompetitionFilter> {

    public CompetitionDao(CompetitionRepository repository) {
        super(repository);
    }
}
