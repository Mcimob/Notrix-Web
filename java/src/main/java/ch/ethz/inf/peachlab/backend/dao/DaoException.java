package ch.ethz.inf.peachlab.backend.dao;

import java.io.Serial;

public class DaoException extends Exception {
    @Serial
    private static final long serialVersionUID = -6095168645195353592L;

    public DaoException(String message, Throwable cause) {
      super(message, cause);
    }

    public DaoException(String message) {
        super(message);
    }
}
