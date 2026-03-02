package ch.ethz.inf.peachlab.ui.webstorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public interface HasUploadedCompetitions extends HasWebStorage {

    String UPLOADED_COMPETITIONS = "uploadedCompetitions";

    default void getUploadedCompetitions(Consumer<Set<String>> consumer) {
        getUploadedCompetitions(consumer, e -> {});
    }

    default void getUploadedCompetitions(Consumer<Set<String>> consumer, Consumer<JsonProcessingException> exceptionConsumer) {
        getItem(consumer, exceptionConsumer, new TypeReference<Set<String>>() {}, UPLOADED_COMPETITIONS, new HashSet<>());
    }

    default void setUploadedCompetitions(Set<String> uploadedCompetitions) {
        setUploadedCompetitions(uploadedCompetitions, e -> {});
    }

    default void setUploadedCompetitions(Set<String> uploadedCompetitions, Consumer<JsonProcessingException> exceptionConsumer) {
        setItem(uploadedCompetitions, exceptionConsumer, UPLOADED_COMPETITIONS);
    }
}
