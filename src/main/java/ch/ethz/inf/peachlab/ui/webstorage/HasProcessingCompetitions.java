package ch.ethz.inf.peachlab.ui.webstorage;

import ch.ethz.inf.peachlab.model.dto.ProcessingCompetition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface HasProcessingCompetitions extends HasWebStorage {

    String PROCESSING_COMPETITIONS = "processingCompetitions";

    default void getProcessingCompetitions(Consumer<Map<String, ProcessingCompetition>> consumer) {
        getProcessingCompetitions(consumer, e -> {});
    }

    default void getProcessingCompetitions(Consumer<Map<String, ProcessingCompetition>> consumer, Consumer<JsonProcessingException> exceptionConsumer) {
        getItem(consumer, exceptionConsumer, new TypeReference<Map<String, ProcessingCompetition>>() {}, PROCESSING_COMPETITIONS, new HashMap<>());
    }

    default void setProcessingCompetitions(Map<String, ProcessingCompetition> processingCompetitions) {
        setProcessingCompetitions(processingCompetitions, e -> {});
    }

    default void setProcessingCompetitions(Map<String, ProcessingCompetition> processingCompetitions, Consumer<JsonProcessingException> exceptionConsumer) {
        setItem(processingCompetitions, exceptionConsumer, PROCESSING_COMPETITIONS);
    }
}
