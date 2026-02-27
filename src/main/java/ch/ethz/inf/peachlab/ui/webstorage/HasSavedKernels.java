package ch.ethz.inf.peachlab.ui.webstorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public interface HasSavedKernels extends HasWebStorage {

    String SAVED_KERNELS = "savedKernels";

    default void getSavedKernels(Consumer<Set<Long>> consumer) {
        getSavedKernels(consumer, e -> {});
    }

    default void getSavedKernels(Consumer<Set<Long>> consumer, Consumer<JsonProcessingException> exceptionConsumer) {
        getItem(consumer, exceptionConsumer, new TypeReference<Set<Long>>() {}, SAVED_KERNELS, new HashSet<>());
    }

    default void setSavedKernels(Set<Long> savedKernels) {
        setSavedKernels(savedKernels, e -> {});
    }

    default void setSavedKernels(Set<Long> savedKernels, Consumer<JsonProcessingException> exceptionConsumer) {
        setItem(savedKernels, exceptionConsumer, SAVED_KERNELS);
    }
}
