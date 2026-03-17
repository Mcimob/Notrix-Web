package ch.ethz.inf.peachlab.backend.dao.db.impl;

import ch.ethz.inf.peachlab.backend.dao.db.BaseDao;
import ch.ethz.inf.peachlab.backend.repository.CompetitionClusterRepository;
import ch.ethz.inf.peachlab.model.entity.CompetitionClusterEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionClusterFilter;
import org.springframework.stereotype.Component;

@Component
public class CompetitionClusterDao extends BaseDaoImpl<CompetitionClusterEntity, CompetitionClusterFilter, Long> implements BaseDao<CompetitionClusterEntity, CompetitionClusterFilter, Long> {
    public CompetitionClusterDao(CompetitionClusterRepository repository) {
        super(repository);
    }
}
