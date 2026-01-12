package ch.ethz.inf.peachlab.backend.dao.impl;

import ch.ethz.inf.peachlab.backend.repository.KernelRepository;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import org.springframework.stereotype.Component;

@Component
public class KernelDaoImpl extends BaseDaoImpl<KernelEntity, KernelFilter> {

    public KernelDaoImpl(KernelRepository repository) {
        super(repository);
    }
}
