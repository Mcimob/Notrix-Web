package ch.ethz.inf.peachlab.ui;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.ui.views.HasNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.page.WebStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public interface HasSavedKernels extends HasLogger, HasNotification {

    default void getSavedKernels(Consumer<Set<Long>> consumer) {
        getSavedKernels(consumer, e -> {});
    }

    default void getSavedKernels(Consumer<Set<Long>> consumer, Consumer<JsonProcessingException> exceptionConsumer) {
        ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);
        WebStorage.getItem("savedKernels", value -> {
            Set<Long> savedKernels;
            if (value == null) {
                savedKernels = new HashSet<>();
            } else {
                try {
                    savedKernels = objectMapper.readValue(value, new TypeReference<Set<Long>>() {});
                } catch (JsonProcessingException e) {
                    getLogger().error("Could not parse savedKernels array {}", value, e);
                    showErrorNotification("Could not fetch saved Notebooks");
                    exceptionConsumer.accept(e);
                    return;
                }
            }
            consumer.accept(savedKernels);
        });
    }
}
