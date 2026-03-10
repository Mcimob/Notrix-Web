package ch.ethz.inf.peachlab.backend.service.rest.impl;

import ch.ethz.inf.peachlab.backend.dao.DaoException;
import ch.ethz.inf.peachlab.backend.dao.rest.NotebookProcessingDao;
import ch.ethz.inf.peachlab.backend.dao.rest.NotebookProcessingNotFinishedException;
import ch.ethz.inf.peachlab.backend.dao.rest.RestException;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingMonitorService;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingService;
import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.model.dto.ProcessingCompetitionMetadata;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatusResponse;
import ch.ethz.inf.peachlab.model.rest.UploadedNotebook;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.streams.UploadMetadata;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
            String identifier = dao.startNotebookProcessing(new UploadedNotebook(uploadedNotebook.cells(), ""));
            monitorService.monitorNotebookProcessing(identifier);
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
    public ServiceResponse<ProcessingStatusResponse<UploadedKernelEntity>> getProcessingStatus(String identifier) {
        ServiceResponse<ProcessingStatusResponse<UploadedKernelEntity>> response = new ServiceResponse<>();

        try {
            ProcessingStatusResponse<UploadedKernelEntity> processingResponse = dao.getProcessingResponse(identifier);
            response.setEntity(processingResponse);
        } catch (RestException e) {
            getLogger().error("Something went wrong while getting the processing status for {}", identifier, e);
            response.addErrorMessage("service.notebookProcessing.error.server");
        } catch (NotebookProcessingNotFinishedException e) {
            response.setEntity(new ProcessingStatusResponse<>(e.getProcessingStatus(), null));
        }

        return response;
    }

    @Override
    public ServiceResponse<Pair<String, String>> startCompetitionProcessing(UploadMetadata meta, byte[] data) {
        ServiceResponse<Pair<String, String>> response = new ServiceResponse<>();
        try {
            List<UploadedNotebook> nbs = new ArrayList<>();
            String description = "";
            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                String[] filenameParts = entry.getName().split("/");
                String filename = filenameParts[filenameParts.length - 1];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                zis.transferTo(out);
                if (filename.equalsIgnoreCase("readme.md")) {
                    description = out.toString();
                }
                if (filename.endsWith(".ipynb")) {
                    UploadedNotebook nb = objectMapper.readValue(out.toByteArray(), UploadedNotebook.class);
                    nbs.add(new UploadedNotebook(nb.cells(), filename.split("\\.")[0]));
                }
            }

            if (nbs.size() < 2) {
                response.addErrorMessage("service.notebooksProcessing.error.numNotebooks");
                return response;
            }

            String identifier = dao.startCompetitionProcessing(nbs);
            monitorService.monitorCompetitionProcessing(identifier);
            response.setEntity(Pair.of(identifier, description));
        } catch (IOException e) {
            getLogger().error("Something went wrong while reading uploaded data", e);
            response.addErrorMessage("service.notebookProcessing.error.data");
        } catch (DaoException e) {
            getLogger().error("Something went wrong while sending the uploaded notebooks", e);
            response.addErrorMessage("service.notebookProcessing.error.server");
        }
        return response;
    }

    @Override
    public ServiceResponse<ProcessingStatusResponse<UploadedCompetitionEntity>> getCompetitionProcessingStatus(String identifier) {
        return null;
    }
}
