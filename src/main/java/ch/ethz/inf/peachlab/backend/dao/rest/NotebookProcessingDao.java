package ch.ethz.inf.peachlab.backend.dao.rest;

import ch.ethz.inf.peachlab.app.NotebookProcessingConfiguration;
import ch.ethz.inf.peachlab.backend.dao.DaoException;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatusResponse;
import ch.ethz.inf.peachlab.model.rest.StartProcessingResponse;
import ch.ethz.inf.peachlab.model.rest.UploadedNotebook;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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
        try {
            StartProcessingResponse result = getRestClient()
                .post()
                .uri(config.getNotebookPath())
                .body(notebook)
                .retrieve()
                .body(StartProcessingResponse.class);
            if (result == null) {
                throw new RestException("Returned result was null");
            }
            return result.jobId();
        } catch (RestException e) {
            throw e;
        } catch(Exception e) {
            throw new RestException("Something went wrong while starting notebook processing", e);
        }
    }

    public ProcessingStatusResponse getProcessingResponse(String identifier)
        throws RestException, NotebookProcessingNotFinishedException {
        try {
            ProcessingStatusResponse result = getRestClient()
                .get()
                .uri(uri -> uri
                    .path(config.getNotebookPath())
                    .path("/" + identifier)
                    .build())
                .retrieve()
                .body(ProcessingStatusResponse.class);
            if (result == null || result.status() == null) {
                throw new RestException("Returned result was null");
            }
            if (result.result() == null) {
                throw new NotebookProcessingNotFinishedException("Processing not yet done", result.status());
            }
            return result;
        } catch (DaoException e) {
            throw e;
        } catch(Exception e) {
            throw new RestException("Something went wrong while fetching notebook processing status", e);
        }
    }

}
