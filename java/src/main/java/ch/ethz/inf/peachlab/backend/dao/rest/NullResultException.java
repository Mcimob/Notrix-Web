package ch.ethz.inf.peachlab.backend.dao.rest;

public class NullResultException extends RestException {
    public NullResultException(String message) {
        super(message);
    }
}
