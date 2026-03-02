package ch.ethz.inf.peachlab.backend.service.rest;

import ch.ethz.inf.peachlab.backend.dao.DaoException;

public interface NotebookProcessingMonitorService {

    void monitorNotebookProcessing(String identifier) throws DaoException;

    void monitorCompetitionProcessing(String identifier) throws DaoException;
}
