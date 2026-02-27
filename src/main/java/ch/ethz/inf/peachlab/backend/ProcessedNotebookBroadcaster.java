package ch.ethz.inf.peachlab.backend;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.Map;

public class ProcessedNotebookBroadcaster {

    static Map<String, SerializableConsumer<String>> listeners = new HashMap<>();
    static Map<String, UI> uiMap = new HashMap<>();

    public static synchronized Registration register(
        SerializableConsumer<String> listener, String identifier, UI ui) {
        listeners.put(identifier, listener);
        uiMap.put(identifier, ui);
        return () -> {
            synchronized (ProcessedNotebookBroadcaster.class) {
                listeners.remove(identifier);
                uiMap.remove(identifier);
            }
        };
    }

    public static synchronized void broadcast(String identifier) {
        if (listeners.containsKey(identifier) && uiMap.containsKey(identifier)) {
            UI ui = uiMap.get(identifier);
            ui.access(() -> {
                if (ui.isAttached()) {
                    listeners.get(identifier).accept(identifier);
                }
            });
        }
    }
}
