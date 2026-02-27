package ch.ethz.inf.peachlab.backend.service.rest;

import ch.ethz.inf.peachlab.backend.dao.DaoException;

public interface NotebookProcessingMonitorService {

    void monitorProcessing(String identifier) throws DaoException;
}
