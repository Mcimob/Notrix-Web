package ch.ethz.inf.peachlab.backend.dao.db.impl;

import ch.ethz.inf.peachlab.backend.repository.KernelRepository;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import org.springframework.stereotype.Component;

@Component
public class KernelDaoImpl extends BaseDaoImpl<KernelEntity, KernelFilter, Long> {

    public KernelDaoImpl(KernelRepository repository) {
        super(repository);
    }
}
