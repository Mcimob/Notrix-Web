package ch.ethz.inf.peachlab.backend.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ServiceResponse<T> {

    private T entity;
    private final Set<String> infoMessages = new HashSet<>();
    private final Set<String> errorMessages = new HashSet<>();

    public static <T> ServiceResponse<T> error() {
        ServiceResponse<T> response = new ServiceResponse<>();
        response.addErrorMessage("service.general.error");
        return response;
    }

    public Optional<T> getEntity() {
        return Optional.ofNullable(entity);
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public Set<String> getInfoMessages() {
        return infoMessages;
    }

    public Set<String> getErrorMessages() {
        return errorMessages;
    }

    public void addInfoMessage(String message) {
        infoMessages.add(message);
    }

    public void addErrorMessage(String message) {
        errorMessages.add(message);
    }

    public boolean hasErrorMessages() {
        return !errorMessages.isEmpty();
    }

    public boolean hasInfoMessages() {
        return !infoMessages.isEmpty();
    }
}
