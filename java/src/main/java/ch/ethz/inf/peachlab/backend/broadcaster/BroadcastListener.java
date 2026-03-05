package ch.ethz.inf.peachlab.backend.broadcaster;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;

public record BroadcastListener(SerializableConsumer<String> consumer, UI ui) {
}
