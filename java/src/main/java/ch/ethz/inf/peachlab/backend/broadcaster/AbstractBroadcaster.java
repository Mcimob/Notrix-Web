package ch.ethz.inf.peachlab.backend.broadcaster;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import java.util.Map;

public abstract class AbstractBroadcaster {

    static synchronized <T> Registration register(
        SerializableConsumer<T> listener, String identifier, UI ui, Map<String, BroadcastListener<T>> listeners) {
        listeners.put(identifier, new BroadcastListener<>(listener, ui));
        return () -> {
            synchronized (ProcessingNotebookBroadcaster.class) {
                listeners.remove(identifier);
            }
        };
    }

    static synchronized <T> void broadcast(String identifier, Map<String, BroadcastListener<T>> listeners, T value) {
        if (listeners.containsKey(identifier)) {
            BroadcastListener<T> listener = listeners.get(identifier);
            UI ui = listener.ui();
            ui.access(() -> {
                if (ui.isAttached()) {
                    listener.consumer().accept(value);
                }
            });
        }
    }
}
