package ch.ethz.inf.peachlab.backend.service.rest;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatusResponse;
import com.vaadin.flow.server.streams.UploadMetadata;
import org.springframework.data.util.Pair;

public interface NotebookProcessingService {

    ServiceResponse<String> startNotebookProcessing(byte[] data);

    ServiceResponse<ProcessingStatusResponse<UploadedKernelEntity>> getProcessingStatus(String identifier);

    ServiceResponse<Pair<String, String>> startCompetitionProcessing(UploadMetadata meta, byte[] data);

    ServiceResponse<ProcessingStatusResponse<UploadedCompetitionEntity>> getCompetitionProcessingStatus(String identifier);
}
