package ch.ethz.inf.peachlab.backend.service.db.impl;

import ch.ethz.inf.peachlab.backend.dao.db.impl.UploadedKernelDaoImpl;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.filter.UploadedKernelFilter;
import org.springframework.stereotype.Service;

@Service
public class UploadedKernelServiceImpl extends BaseServiceImpl<UploadedKernelEntity, UploadedKernelFilter, String> implements UploadedKernelService {

    public UploadedKernelServiceImpl(UploadedKernelDaoImpl dao) {
        super(dao);
    }
}
