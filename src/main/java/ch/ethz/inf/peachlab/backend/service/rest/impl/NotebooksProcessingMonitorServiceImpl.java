package ch.ethz.inf.peachlab.backend.service.rest.impl;

import ch.ethz.inf.peachlab.backend.ProcessedNotebookBroadcaster;
import ch.ethz.inf.peachlab.backend.dao.rest.NotebookProcessingDao;
import ch.ethz.inf.peachlab.backend.dao.rest.NotebookProcessingNotFinishedException;
import ch.ethz.inf.peachlab.backend.dao.rest.RestException;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingMonitorService;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatusResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotebooksProcessingMonitorServiceImpl implements NotebookProcessingMonitorService {

    private final UploadedKernelService kernelService;
    private final NotebookProcessingDao dao;

    public NotebooksProcessingMonitorServiceImpl(UploadedKernelService kernelService, NotebookProcessingDao dao) {
        this.kernelService = kernelService;
        this.dao = dao;
    }

    @Async
    @Retryable(
        retryFor = NotebookProcessingNotFinishedException.class,
        maxAttempts = 300,
        backoff = @Backoff(delay = 1000)
    )
    public void monitorProcessing(String identifier) throws NotebookProcessingNotFinishedException, RestException {
        ProcessingStatusResponse status = dao.getProcessingResponse(identifier);

        UploadedKernelEntity kernel = status.result();
        kernel.setId(identifier);
        kernel.setCreationDate(LocalDateTime.now());
        kernelService.save(kernel);

        ProcessedNotebookBroadcaster.broadcast(identifier);
    }
}
