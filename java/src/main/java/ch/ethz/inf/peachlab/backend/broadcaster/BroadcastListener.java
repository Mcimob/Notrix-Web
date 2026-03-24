package ch.ethz.inf.peachlab.backend.broadcaster;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.Map;

public record BroadcastListener<T>(SerializableConsumer<T> consumer, UI ui) {
}
