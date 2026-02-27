package ch.ethz.inf.peachlab.backend.dao.rest;

import ch.ethz.inf.peachlab.backend.dao.DaoException;

import java.io.Serial;

public class RestException extends DaoException {
    @Serial
    private static final long serialVersionUID = 743896721276188861L;

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestException(String message) {
        super(message);
    }
}
