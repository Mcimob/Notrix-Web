package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.UploadedClusterService;
import ch.ethz.inf.peachlab.backend.service.db.UploadedCompetitionService;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.model.entity.UploadedClusterEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.filter.UploadedClusterFilter;
import ch.ethz.inf.peachlab.model.filter.UploadedCompetitionFilter;
import ch.ethz.inf.peachlab.model.filter.UploadedKernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.HasLoadType;
import ch.ethz.inf.peachlab.model.loadtype.UploadedClusterLoadType;
import ch.ethz.inf.peachlab.model.loadtype.UploadedKernelLoadType;
import ch.ethz.inf.peachlab.ui.MainLayout;
import com.vaadin.flow.router.Route;

import java.io.Serial;

@Route(value = "uploadedCompetition", layout = MainLayout.class)
public class UploadedCompetitionView extends AbstractCompetitionView<UploadedCompetitionEntity, UploadedKernelEntity, UploadedClusterEntity, UploadedKernelFilter, UploadedClusterFilter, UploadedCompetitionFilter, String> {
    @Serial
    private static final long serialVersionUID = 1605455693446484673L;

    protected UploadedCompetitionView(
        UploadedCompetitionService competitionService,
      UploadedKernelService kernelService,
      UploadedClusterService clusterService) {
        super(competitionService, kernelService, clusterService, new UploadedKernelFilter(), new UploadedClusterFilter());
    }

    @Override
    protected HasLoadType getKernelLoadType() {
        return UploadedKernelLoadType.WITH_CELLS;
    }

    @Override
    protected HasLoadType getClusterLoadType() {
        return UploadedClusterLoadType.WITH_KERNELS_AND_CELLS;
    }

    @Override
    protected String parseId(String stringId) {
        return stringId;
    }

    @Override
    protected ServiceResponse<UploadedCompetitionEntity> getInitResponse(String parameter) {
        return competitionService.fetchById(parameter);
    }

    @Override
    public void render() {
        super.render();
        initData();
    }
}
