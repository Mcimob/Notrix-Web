package ch.ethz.inf.peachlab.backend.service.rest.impl;

import ch.ethz.inf.peachlab.backend.dao.DaoException;
import ch.ethz.inf.peachlab.backend.dao.rest.NotebookProcessingDao;
import ch.ethz.inf.peachlab.backend.dao.rest.NotebookProcessingNotFinishedException;
import ch.ethz.inf.peachlab.backend.dao.rest.RestException;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingMonitorService;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingService;
import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatusResponse;
import ch.ethz.inf.peachlab.model.rest.UploadedNotebook;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class NotebookProcessingServiceImpl implements NotebookProcessingService, HasLogger {

    private final NotebookProcessingDao dao;
    private final ObjectMapper objectMapper;
    private final NotebookProcessingMonitorService monitorService;

    public NotebookProcessingServiceImpl(NotebookProcessingDao dao, ObjectMapper objectMapper, NotebookProcessingMonitorService monitorService) {
        this.dao = dao;
        this.objectMapper = objectMapper;
        this.monitorService = monitorService;
    }

    @Override
    public ServiceResponse<String> startNotebookProcessing(byte[] data) {
        ServiceResponse<String> response = new ServiceResponse<>();

        try {
            UploadedNotebook uploadedNotebook = objectMapper.readValue(data, UploadedNotebook.class);
            String identifier = dao.startNotebookProcessing(uploadedNotebook);
            monitorService.monitorProcessing(identifier);
            response.setEntity(identifier);
        } catch (IOException e) {
            getLogger().error("Something went wrong while reading uploaded data", e);
            response.addErrorMessage("service.notebookProcessing.error.data");
        } catch (DaoException e) {
            getLogger().error("Something went wrong while sending the uploaded notebook", e);
            response.addErrorMessage("service.notebookProcessing.error.server");
        }

        return response;
    }

    @Override
    public ServiceResponse<ProcessingStatusResponse> getProcessingStatus(String identifier) {
        ServiceResponse<ProcessingStatusResponse> response = new ServiceResponse<>();

        try {
            ProcessingStatusResponse processingResponse = dao.getProcessingResponse(identifier);
            response.setEntity(processingResponse);
        } catch (RestException e) {
            getLogger().error("Something went wrong while getting the processing status for {}", identifier, e);
            response.addErrorMessage("service.notebookProcessing.error.server");
        } catch (NotebookProcessingNotFinishedException e) {
            response.setEntity(new ProcessingStatusResponse(e.getProcessingStatus(), null));
        }

        return response;
    }
}
