package ch.ethz.inf.peachlab.ui.webstorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface HasUploadedNotebooks extends HasWebStorage {

    String UPLOADED_NOTEBOOKS = "uploadedNotebooks";

    default void getUploadedNotebooks(Consumer<Map<Long, Set<String>>> consumer) {
        getUploadedNotebooks(consumer, e -> {});
    }

    default void getUploadedNotebooks(Consumer<Map<Long, Set<String>>> consumer, Consumer<JsonProcessingException> exceptionConsumer) {
        getItem(consumer, exceptionConsumer, new TypeReference<Map<Long, Set<String>>>() {}, UPLOADED_NOTEBOOKS, new HashMap<>());
    }

    default void setUploadedNotebooks(Map<Long, Set<String>> uploadedNotebooks) {
        setUploadedNotebooks(uploadedNotebooks, e -> {});
    }

    default void setUploadedNotebooks(Map<Long, Set<String>> uploadedNotebooks, Consumer<JsonProcessingException> exceptionConsumer) {
        setItem(uploadedNotebooks, exceptionConsumer, UPLOADED_NOTEBOOKS);
    }
}
