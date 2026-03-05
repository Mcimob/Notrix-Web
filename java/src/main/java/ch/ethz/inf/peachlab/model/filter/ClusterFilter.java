package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

public class ClusterFilter extends AbstractClusterFilter<ClusterEntity, KernelEntity, CompetitionEntity> {

    @Serial
    private static final long serialVersionUID = 5973223330744160086L;
}
