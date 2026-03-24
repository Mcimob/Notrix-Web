package ch.ethz.inf.peachlab.backend.service.rest.impl;

import ch.ethz.inf.peachlab.backend.broadcaster.ProcessingCompetitionBroadcaster;
import ch.ethz.inf.peachlab.backend.broadcaster.ProcessingCompetitionUpdateBroadcaster;
import ch.ethz.inf.peachlab.backend.broadcaster.ProcessingNotebookBroadcaster;
import ch.ethz.inf.peachlab.backend.broadcaster.ProcessingNotebookUpdateBroadcaster;
import ch.ethz.inf.peachlab.backend.dao.DaoException;
import ch.ethz.inf.peachlab.backend.dao.rest.NotebookProcessingDao;
import ch.ethz.inf.peachlab.backend.dao.rest.NotebookProcessingNotFinishedException;
import ch.ethz.inf.peachlab.backend.dao.rest.NullResultException;
import ch.ethz.inf.peachlab.backend.dao.rest.RestException;
import ch.ethz.inf.peachlab.backend.service.db.UploadedCompetitionService;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingMonitorService;
import ch.ethz.inf.peachlab.model.entity.HasClusterData;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatus;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatusResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotebooksProcessingMonitorServiceImpl implements NotebookProcessingMonitorService {

    private final UploadedKernelService kernelService;
    private final UploadedCompetitionService competitionService;
    private final NotebookProcessingDao dao;

    public NotebooksProcessingMonitorServiceImpl(UploadedKernelService kernelService, UploadedCompetitionService competitionService, NotebookProcessingDao dao) {
        this.kernelService = kernelService;
        this.competitionService = competitionService;
        this.dao = dao;
    }

    @Override
    @Async
    @Retryable(
        retryFor = NotebookProcessingNotFinishedException.class,
        maxAttempts = 300,
        backoff = @Backoff(delay = 1000)
    )
    public void monitorNotebookProcessing(String identifier) throws NotebookProcessingNotFinishedException, RestException {
        ProcessingStatusResponse<UploadedKernelEntity> status;
        try {
            status = dao.getProcessingResponse(identifier);
        } catch (NotebookProcessingNotFinishedException e) {
            ProcessingNotebookUpdateBroadcaster.broadcast(identifier, e.getProcessingStatus());
            throw e;
        } catch (NullResultException e) {
            ProcessingNotebookUpdateBroadcaster.broadcast(identifier, ProcessingStatus.DONE);
            return;
        }

        UploadedKernelEntity kernel = status.result();
        kernel.setId(identifier);
        kernel.setCreationDate(LocalDateTime.now());
        kernelService.save(kernel);

        ProcessingNotebookBroadcaster.broadcast(identifier);
        ProcessingNotebookUpdateBroadcaster.broadcast(identifier, ProcessingStatus.DONE);
    }

    @Override
    @Async
    @Retryable(
        retryFor = NotebookProcessingNotFinishedException.class,
        maxAttempts = 300,
        backoff = @Backoff(delay = 1000)
    )
    public void monitorCompetitionProcessing(String identifier) throws DaoException {
        ProcessingStatusResponse<UploadedCompetitionEntity> status;
        try {
            status = dao.getCompetitionProcessingResponse(identifier);
        } catch (NotebookProcessingNotFinishedException e) {
            ProcessingCompetitionUpdateBroadcaster.broadcast(identifier, e.getProcessingStatus());
            throw e;
        } catch (NullResultException e) {
            ProcessingCompetitionUpdateBroadcaster.broadcast(identifier, ProcessingStatus.DONE);
            return;
        }

        UploadedCompetitionEntity competition = status.result();
        competition.setId(identifier);
        competition.setTotalSubmissions(competition.getClusters().stream()
            .map(HasClusterData::getKernels)
            .mapToLong(Collection::size)
            .sum());
        Set<UploadedKernelEntity> kernels = competition.getClusters().stream()
            .map(HasClusterData::getKernels)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        competition.setKernels(kernels);
        kernels.forEach(k -> {
                k.setCreationDate(LocalDateTime.now());
                k.setSourceCompetitionId(null);
                k.setId(UUID.randomUUID().toString());
            });


        competitionService.save(competition);

        ProcessingCompetitionBroadcaster.broadcast(identifier);
        ProcessingCompetitionUpdateBroadcaster.broadcast(identifier, ProcessingStatus.DONE);
    }
}
