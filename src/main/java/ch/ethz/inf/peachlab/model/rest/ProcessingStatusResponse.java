package ch.ethz.inf.peachlab.model.rest;

public record ProcessingStatusResponse<T>(ProcessingStatus status, T result) {
}
