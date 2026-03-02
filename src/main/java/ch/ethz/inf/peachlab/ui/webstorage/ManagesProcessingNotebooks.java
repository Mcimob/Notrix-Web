package ch.ethz.inf.peachlab.ui.webstorage;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.UploadedCompetitionService;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.model.dto.ProcessingCompetition;
import ch.ethz.inf.peachlab.model.dto.ProcessingNotebook;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface ManagesProcessingNotebooks extends
    HasUploadedNotebooks, HasProcessingNotebooks,
    HasUploadedCompetitions, HasProcessingCompetitions {

    default void onNotebooksProcessingDone(String identifier) {
        UploadedKernelService kernelService = SpringContext.getBean(UploadedKernelService.class);
        getProcessingNotebooks(nbs -> {
            ProcessingNotebook nb = nbs.get(identifier);

            ServiceResponse<UploadedKernelEntity> response = kernelService.fetchById(identifier);
            if (response.getEntity().isEmpty()) {
                showErrorNotification("Could not find notebook in database. Please check your uploaded notebooks under the 'Save' page");
                return;
            }
            UploadedKernelEntity kernel = response.getEntity().get();
            kernel.setTitle(nb.name());
            kernel.setSourceCompetitionId(nb.competitionId());
            kernelService.save(kernel);

            getUploadedNotebooks(uploadedNbs -> {
                uploadedNbs.putIfAbsent(nb.competitionId(), new HashSet<>());
                uploadedNbs.get(kernel.getSourceCompetitionId()).add(kernel.getId());
                setUploadedNotebooks(uploadedNbs);
            });

            nbs.remove(identifier);
            setProcessingNotebooks(nbs);

            showSuccessNotification("Your notebook {0} is finished processing. You can take a look at it on the 'Saved' page", nb.name());
        });
    }

    default void onCompetitionProcessingDone(String identifier) {
        UploadedCompetitionService competitionService = SpringContext.getBean(UploadedCompetitionService.class);
        getProcessingCompetitions(comps -> {
            ProcessingCompetition comp = comps.get(identifier);

            ServiceResponse<UploadedCompetitionEntity> response = competitionService.fetchById(identifier);
            if (response.getEntity().isEmpty()) {
                showErrorNotification("Could not find notebook in database. Please check your uploaded notebooks under the 'Save' page");
                return;
            }
            UploadedCompetitionEntity competition = response.getEntity().get();
            competition.setTitle(comp.name());
            competition.setOverview(comp.description());
            competitionService.save(competition);

            getUploadedCompetitions(uploadedComps -> {
                uploadedComps.add(competition.getId());
                setUploadedCompetitions(uploadedComps);
            });

            comps.remove(identifier);
            setProcessingCompetitions(comps);

            showSuccessNotification("Your Competition {0} is finished processing. You can take a look at it on the 'Saved' page", comp.name());
        });
    }

}
