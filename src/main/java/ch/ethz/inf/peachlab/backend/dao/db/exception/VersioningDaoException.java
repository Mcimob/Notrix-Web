package ch.ethz.inf.peachlab.backend.dao.db.exception;

import ch.ethz.inf.peachlab.backend.dao.DaoException;

import java.io.Serial;

public class VersioningDaoException extends DaoException {
    @Serial
    private static final long serialVersionUID = 7285227627767797894L;

    public VersioningDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
