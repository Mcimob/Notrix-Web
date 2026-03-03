package ch.ethz.inf.peachlab.model.filter;

import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

public class UploadedKernelFilter extends AbstractKernelFilter<UploadedKernelEntity, String, UploadedCompetitionEntity> {
    @Serial
    private static final long serialVersionUID = 4781337022644559230L;
}
