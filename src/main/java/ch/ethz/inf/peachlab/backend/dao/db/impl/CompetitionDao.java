package ch.ethz.inf.peachlab.backend.dao.db.impl;

import ch.ethz.inf.peachlab.backend.repository.CompetitionRepository;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import org.springframework.stereotype.Component;

@Component
public class CompetitionDao extends BaseDaoImpl<CompetitionEntity, CompetitionFilter, Long> {

    public CompetitionDao(CompetitionRepository repository) {
        super(repository);
    }
}
