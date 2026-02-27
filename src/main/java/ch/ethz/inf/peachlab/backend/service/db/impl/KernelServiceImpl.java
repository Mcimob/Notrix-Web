package ch.ethz.inf.peachlab.backend.service.db.impl;

import ch.ethz.inf.peachlab.backend.dao.db.impl.KernelDaoImpl;
import ch.ethz.inf.peachlab.backend.service.db.KernelService;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import org.springframework.stereotype.Service;

@Service
public class KernelServiceImpl extends BaseServiceImpl<KernelEntity, KernelFilter, Long> implements KernelService {

    public KernelServiceImpl(KernelDaoImpl dao) {
        super(dao);
    }
}
