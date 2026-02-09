package ch.ethz.inf.peachlab.backend.dao.impl;

import ch.ethz.inf.peachlab.backend.repository.ClusterRepository;
import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.filter.ClusterFilter;
import org.springframework.stereotype.Component;

@Component
public class ClusterDao extends BaseDaoImpl<ClusterEntity, ClusterFilter> {
    public ClusterDao(ClusterRepository repository) {
        super(repository);
    }
}
