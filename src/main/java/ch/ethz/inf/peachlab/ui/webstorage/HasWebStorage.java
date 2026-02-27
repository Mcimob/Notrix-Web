package ch.ethz.inf.peachlab.ui.webstorage;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.ui.views.HasNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.page.WebStorage;

import java.util.function.Consumer;

public interface HasWebStorage extends HasLogger, HasNotification {

    default <T> void getItem(Consumer<T> consumer,
                             Consumer<JsonProcessingException> exceptionConsumer,
                             TypeReference<T> typeReference,
                             String name,
                             T defaultValue) {
        ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);
        WebStorage.getItem(name, value -> {
            T item;
            if (value == null) {
                item = defaultValue;
            } else {
                try {
                    item = objectMapper.readValue(value, typeReference);
                } catch (JsonProcessingException e) {
                    getLogger().error("Could not parse item {}: {}", name, value, e);
                    showErrorNotification("Could not fetch {0}", name);
                    exceptionConsumer.accept(e);
                    return;
                }
            }
            consumer.accept(item);
        });
    }

    default <T> void setItem(T item, Consumer<JsonProcessingException> exceptionConsumer, String name) {
        ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);
        try {
            String itemString = objectMapper.writeValueAsString(item);
            WebStorage.setItem(name, itemString);
        } catch (JsonProcessingException e) {
            getLogger().error("Could not dump item {}: {}", name, item, e);
            showErrorNotification("Could not update {0}", name);
            exceptionConsumer.accept(e);
        }
    }
}
