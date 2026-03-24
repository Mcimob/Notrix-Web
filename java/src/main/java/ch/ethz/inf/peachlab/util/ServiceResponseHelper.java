package ch.ethz.inf.peachlab.util;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;

import java.util.function.Function;

public class ServiceResponseHelper {

    public static <S, T> ServiceResponse<T> transformEntity(ServiceResponse<S> response, Function<S, T> mapper) {
        ServiceResponse<T> result = new ServiceResponse<>();
        response.getErrorMessages().forEach(result::addErrorMessage);
        response.getInfoMessages().forEach(result::addInfoMessage);
        response.getEntity().map(mapper).ifPresent(result::setEntity);

        return result;
    }
}
