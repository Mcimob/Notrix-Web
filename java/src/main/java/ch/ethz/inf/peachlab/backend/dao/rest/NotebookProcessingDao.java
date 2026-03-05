package ch.ethz.inf.peachlab.backend.dao.rest;

import ch.ethz.inf.peachlab.app.NotebookProcessingConfiguration;
import ch.ethz.inf.peachlab.backend.dao.DaoException;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatusResponse;
import ch.ethz.inf.peachlab.model.rest.StartProcessingResponse;
import ch.ethz.inf.peachlab.model.rest.UploadedNotebook;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class NotebookProcessingDao {

    private final NotebookProcessingConfiguration config;

    public NotebookProcessingDao(NotebookProcessingConfiguration config) {
        this.config = config;
    }

    private RestClient getRestClient() {
        return RestClient.create(config.getBaseUrl());
    }

    public String startNotebookProcessing(UploadedNotebook notebook) throws RestException {
        StartProcessingResponse result;
        try {
            result = getRestClient()
                .post()
                .uri(config.getNotebookPath())
                .body(notebook)
                .retrieve()
                .body(StartProcessingResponse.class);
        } catch (Exception e) {
            throw new RestException("Something went wrong while starting notebook processing", e);
        }

        if (result == null) {
            throw new RestException("Returned result was null");
        }
        return result.jobId();
    }

    public ProcessingStatusResponse<UploadedKernelEntity> getProcessingResponse(String identifier)
        throws RestException, NotebookProcessingNotFinishedException {
        ProcessingStatusResponse<UploadedKernelEntity> result;
        try {
            result = getRestClient()
                .get()
                .uri(uri -> uri
                    .path(config.getNotebookPath())
                    .path("/" + identifier)
                    .build())
                .retrieve()
                .body(new ParameterizedTypeReference<ProcessingStatusResponse<UploadedKernelEntity>>() {});
        } catch(Exception e) {
            throw new RestException("Something went wrong while fetching notebook processing status", e);
        }

        if (result == null || result.status() == null) {
            throw new RestException("Returned result was null");
        }
        if (result.result() == null) {
            throw new NotebookProcessingNotFinishedException("Processing not yet done", result.status());
        }
        return result;
    }

    public String startCompetitionProcessing(List<UploadedNotebook> notebooks) throws RestException {
        StartProcessingResponse result;
        try {
            result = getRestClient()
                .post()
                .uri(config.getCompetitionPath())
                .body(notebooks)
                .retrieve()
                .body(StartProcessingResponse.class);
        } catch (Exception e) {
            throw new RestException("Something went wrong while starting competition processing", e);
        }

        if  (result == null) {
            throw new RestException("Returned result was null");
        }
        return result.jobId();
    }

    public ProcessingStatusResponse<UploadedCompetitionEntity> getCompetitionProcessingResponse(String identifier) throws RestException, NotebookProcessingNotFinishedException {
        ProcessingStatusResponse<UploadedCompetitionEntity> result;
        try {
            result = getRestClient()
                .get()
                .uri(uri -> uri
                    .path(config.getCompetitionPath())
                    .path("/" + identifier)
                    .build())
                .retrieve()
                .body(new ParameterizedTypeReference<ProcessingStatusResponse<UploadedCompetitionEntity>>() {});
        } catch(Exception e) {
            throw new RestException("Something went wrong while fetching notebook processing status", e);
        }

        if (result == null || result.status() == null) {
            throw new RestException("Returned result was null");
        }
        if (result.result() == null) {
            throw new NotebookProcessingNotFinishedException("Processing not yet done", result.status());
        }
        return result;
    }

}
