package ch.ethz.inf.peachlab.backend.service.rest;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatusResponse;

public interface NotebookProcessingService {

    ServiceResponse<String> startNotebookProcessing(byte[] data);

    ServiceResponse<ProcessingStatusResponse> getProcessingStatus(String identifier);
}
