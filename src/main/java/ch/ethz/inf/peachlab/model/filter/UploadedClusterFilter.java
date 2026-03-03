package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.UploadedClusterEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;

import java.io.Serial;

public class UploadedClusterFilter extends AbstractClusterFilter<UploadedClusterEntity, UploadedKernelEntity, UploadedCompetitionEntity> {
    @Serial
    private static final long serialVersionUID = -7257074385264027448L;
}
