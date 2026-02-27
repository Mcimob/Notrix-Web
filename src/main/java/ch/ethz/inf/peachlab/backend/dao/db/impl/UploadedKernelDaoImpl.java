package ch.ethz.inf.peachlab.backend.dao.db.impl;

import ch.ethz.inf.peachlab.backend.repository.UploadedKernelRepository;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.filter.UploadedKernelFilter;
import org.springframework.stereotype.Component;

@Component
public class UploadedKernelDaoImpl extends BaseDaoImpl<UploadedKernelEntity, UploadedKernelFilter, String> {

    public UploadedKernelDaoImpl(UploadedKernelRepository repository) {
        super(repository);
    }
}
