package ch.ethz.inf.peachlab.backend.dao.rest;

import ch.ethz.inf.peachlab.backend.dao.DaoException;

public class RestException extends DaoException {
    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestException(String message) {
        super(message);
    }
}
