package ch.ethz.inf.peachlab.ui.webstorage;

import ch.ethz.inf.peachlab.model.dto.ProcessingNotebook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface HasProcessingNotebooks extends HasWebStorage {

    String PROCESSING_NOTEBOOKS = "processingNotebooks";

    default void getProcessingNotebooks(Consumer<Map<String, ProcessingNotebook>> consumer) {
        getProcessingNotebooks(consumer, e -> {});
    }

    default void getProcessingNotebooks(Consumer<Map<String, ProcessingNotebook>> consumer, Consumer<JsonProcessingException> exceptionConsumer) {
        getItem(consumer, exceptionConsumer, new TypeReference<Map<String, ProcessingNotebook>>() {}, PROCESSING_NOTEBOOKS, new HashMap<>());
    }

    default void setProcessingNotebooks(Map<String, ProcessingNotebook> processingNotebooks) {
        setProcessingNotebooks(processingNotebooks, e -> {});
    }

    default void setProcessingNotebooks(Map<String, ProcessingNotebook> processingNotebooks, Consumer<JsonProcessingException> exceptionConsumer) {
        setItem(processingNotebooks, exceptionConsumer, PROCESSING_NOTEBOOKS);
    }
}
