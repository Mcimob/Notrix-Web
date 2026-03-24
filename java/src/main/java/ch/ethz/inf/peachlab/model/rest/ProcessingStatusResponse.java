package ch.ethz.inf.peachlab.model.rest;

import java.io.Serializable;

public record ProcessingStatusResponse<T extends Serializable>(ProcessingStatus status, T result) implements Serializable {
}
