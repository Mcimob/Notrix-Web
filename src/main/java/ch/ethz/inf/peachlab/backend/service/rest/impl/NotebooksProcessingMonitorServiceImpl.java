package ch.ethz.inf.peachlab.backend.service.rest.impl;

import ch.ethz.inf.peachlab.backend.broadcaster.ProcessedNotebookBroadcaster;
import ch.ethz.inf.peachlab.backend.dao.DaoException;
import ch.ethz.inf.peachlab.backend.dao.rest.NotebookProcessingDao;
import ch.ethz.inf.peachlab.backend.dao.rest.NotebookProcessingNotFinishedException;
import ch.ethz.inf.peachlab.backend.dao.rest.RestException;
import ch.ethz.inf.peachlab.backend.service.db.UploadedCompetitionService;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingMonitorService;
import ch.ethz.inf.peachlab.model.entity.HasClusterData;
import ch.ethz.inf.peachlab.model.entity.UploadedClusterEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatusResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        ProcessingStatusResponse<UploadedKernelEntity> status = dao.getProcessingResponse(identifier);

        UploadedKernelEntity kernel = status.result();
        kernel.setId(identifier);
        kernel.setCreationDate(LocalDateTime.now());
        kernelService.save(kernel);

        ProcessedNotebookBroadcaster.broadcastNotebooksDone(identifier);
    }

    @Override
    @Async
    @Retryable(
        retryFor = NotebookProcessingNotFinishedException.class,
        maxAttempts = 300,
        backoff = @Backoff(delay = 1000)
    )
    public void monitorCompetitionProcessing(String identifier) throws DaoException {
        ProcessingStatusResponse<UploadedCompetitionEntity> status = dao.getCompetitionProcessingResponse(identifier);

        UploadedCompetitionEntity competition = status.result();
        competition.setId(identifier);
        competition.setTotalSubmissions(competition.getClusters().stream()
            .map(HasClusterData::getKernels)
            .mapToLong(Collection::size)
            .sum());
        competition.getClusters().stream()
            .map(HasClusterData::getKernels)
            .flatMap(Collection::stream)
            .forEach(k -> {
                k.setCreationDate(LocalDateTime.now());
                k.setSourceCompetitionId(null);
            });

        competitionService.save(competition);

        ProcessedNotebookBroadcaster.broadcastCompetitionsDone(identifier);
    }
}
