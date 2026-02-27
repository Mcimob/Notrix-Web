package ch.ethz.inf.peachlab.backend.dao.rest;

import ch.ethz.inf.peachlab.backend.dao.DaoException;
import ch.ethz.inf.peachlab.model.rest.ProcessingStatus;

import java.io.Serial;

public class NotebookProcessingNotFinishedException extends DaoException {
    @Serial
    private static final long serialVersionUID = 5770540190182780762L;

    private final ProcessingStatus processingStatus;

    public NotebookProcessingNotFinishedException(String message, ProcessingStatus processingStatus) {
        super(message);
        this.processingStatus = processingStatus;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }
}
