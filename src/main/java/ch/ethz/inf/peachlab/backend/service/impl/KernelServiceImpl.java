package ch.ethz.inf.peachlab.backend.service.impl;

import ch.ethz.inf.peachlab.backend.dao.impl.KernelDaoImpl;
import ch.ethz.inf.peachlab.backend.service.KernelService;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import org.springframework.stereotype.Service;

@Service
public class KernelServiceImpl extends BaseServiceImpl<KernelEntity, KernelFilter> implements KernelService {

    public KernelServiceImpl(KernelDaoImpl dao) {
        super(dao);
    }
}
